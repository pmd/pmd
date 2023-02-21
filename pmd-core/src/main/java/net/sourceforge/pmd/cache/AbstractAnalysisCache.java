/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.cache.internal.ClasspathFingerprinter;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.reporting.FileAnalysisListener;

/**
 * Abstract implementation of the analysis cache. Handles all operations, except for persistence.
 *
 * @deprecated This is internal API, will be hidden with 7.0.0
 */
@Deprecated
@InternalApi
public abstract class AbstractAnalysisCache implements AnalysisCache {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractAnalysisCache.class);
    protected static final ClasspathFingerprinter FINGERPRINTER = new ClasspathFingerprinter();
    protected final String pmdVersion;
    protected final ConcurrentMap<String, AnalysisResult> fileResultsCache = new ConcurrentHashMap<>();
    protected final ConcurrentMap<String, AnalysisResult> updatedResultsCache = new ConcurrentHashMap<>();
    protected final CachedRuleMapper ruleMapper = new CachedRuleMapper();
    protected long rulesetChecksum;
    protected long auxClassPathChecksum;
    protected long executionClassPathChecksum;

    /**
     * Creates a new empty cache
     */
    public AbstractAnalysisCache() {
        pmdVersion = PMDVersion.VERSION;
    }

    @Override
    public boolean isUpToDate(final TextDocument document) {
        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.ANALYSIS_CACHE, "up-to-date check")) {
            // There is a new file being analyzed, prepare entry in updated cache
            final AnalysisResult updatedResult = new AnalysisResult(document.getCheckSum(), new ArrayList<>());
            updatedResultsCache.put(document.getPathId(), updatedResult);

            // Now check the old cache
            final AnalysisResult analysisResult = fileResultsCache.get(document.getPathId());

            // is this a known file? has it changed?
            final boolean result = analysisResult != null
                && analysisResult.getFileChecksum() == updatedResult.getFileChecksum();

            if (result) {
                LOG.debug("Incremental Analysis cache HIT");
            } else {
                LOG.debug("Incremental Analysis cache MISS - {}",
                          analysisResult != null ? "file changed" : "no previous result found");
            }

            return result;
        }
    }

    @Override
    public List<RuleViolation> getCachedViolations(final TextDocument sourceFile) {
        final AnalysisResult analysisResult = fileResultsCache.get(sourceFile.getPathId());

        if (analysisResult == null) {
            // new file, avoid nulls
            return Collections.emptyList();
        }

        return analysisResult.getViolations();
    }

    @Override
    public void analysisFailed(final TextDocument sourceFile) {
        updatedResultsCache.remove(sourceFile.getPathId());
    }


    /**
     * Returns true if the cache exists. If so, normal cache validity checks
     * will be performed. Otherwise, the cache is necessarily invalid (e.g. on a first run).
     */
    protected abstract boolean cacheExists();


    @Override
    public void checkValidity(final RuleSets ruleSets, final ClassLoader auxclassPathClassLoader) {
        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.ANALYSIS_CACHE, "validity check")) {
            boolean cacheIsValid = cacheExists();

            if (cacheIsValid && ruleSets.getChecksum() != rulesetChecksum) {
                LOG.info("Analysis cache invalidated, rulesets changed.");
                cacheIsValid = false;
            }

            final long currentAuxClassPathChecksum;
            if (auxclassPathClassLoader instanceof URLClassLoader) {
                // we don't want to close our aux classpath loader - we still need it...
                @SuppressWarnings("PMD.CloseResource") final URLClassLoader urlClassLoader = (URLClassLoader) auxclassPathClassLoader;
                currentAuxClassPathChecksum = FINGERPRINTER.fingerprint(urlClassLoader.getURLs());

                if (cacheIsValid && currentAuxClassPathChecksum != auxClassPathChecksum) {
                    // TODO some rules don't need that (in fact, some languages)
                    LOG.info("Analysis cache invalidated, auxclasspath changed.");
                    cacheIsValid = false;
                }
            } else {
                currentAuxClassPathChecksum = 0;
            }

            final long currentExecutionClassPathChecksum = FINGERPRINTER.fingerprint(getClassPathEntries());
            if (cacheIsValid && currentExecutionClassPathChecksum != executionClassPathChecksum) {
                LOG.info("Analysis cache invalidated, execution classpath changed.");
                cacheIsValid = false;
            }

            if (!cacheIsValid) {
                // Clear the cache
                fileResultsCache.clear();
            }

            // Update the local checksums
            rulesetChecksum = ruleSets.getChecksum();
            auxClassPathChecksum = currentAuxClassPathChecksum;
            executionClassPathChecksum = currentExecutionClassPathChecksum;
            ruleMapper.initialize(ruleSets);
        }
    }

    private static boolean isClassPathWildcard(String entry) {
        return entry.endsWith("/*") || entry.endsWith("\\*");
    }

    private URL[] getClassPathEntries() {
        final String classpath = System.getProperty("java.class.path");
        final String[] classpathEntries = classpath.split(File.pathSeparator);
        final List<URL> entries = new ArrayList<>();

        final SimpleFileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file,
                                             final BasicFileAttributes attrs) throws IOException {
                if (!attrs.isSymbolicLink()) { // Broken link that can't be followed
                    entries.add(file.toUri().toURL());
                }
                return FileVisitResult.CONTINUE;
            }
        };
        final SimpleFileVisitor<Path> jarFileVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file,
                                             final BasicFileAttributes attrs) throws IOException {
                String extension = IOUtil.getFilenameExtension(file.toString());
                if ("jar".equalsIgnoreCase(extension)) {
                    fileVisitor.visitFile(file, attrs);
                }
                return FileVisitResult.CONTINUE;
            }
        };

        try {
            for (final String entry : classpathEntries) {
                final File f = new File(entry);
                if (isClassPathWildcard(entry)) {
                    Files.walkFileTree(new File(entry.substring(0, entry.length() - 1)).toPath(),
                                       EnumSet.of(FileVisitOption.FOLLOW_LINKS), 1, jarFileVisitor);
                } else if (f.isFile()) {
                    entries.add(f.toURI().toURL());
                } else if (f.exists()) { // ignore non-existing directories
                    Files.walkFileTree(f.toPath(), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                                       fileVisitor);
                }
            }
        } catch (final IOException e) {
            LOG.error("Incremental analysis can't check execution classpath contents", e);
            throw new RuntimeException(e);
        }

        return entries.toArray(new URL[0]);
    }

    @Override
    public FileAnalysisListener startFileAnalysis(TextDocument file) {
        String fileName = file.getPathId();
        AnalysisResult analysisResult = updatedResultsCache.get(fileName);
        if (analysisResult == null) {
            analysisResult = new AnalysisResult(file.getCheckSum());
        }
        final AnalysisResult nonNullAnalysisResult = analysisResult;

        return new FileAnalysisListener() {
            @Override
            public void onRuleViolation(RuleViolation violation) {
                synchronized (nonNullAnalysisResult) {
                    nonNullAnalysisResult.addViolation(violation);
                }
            }

            @Override
            public void onError(ProcessingError error) {
                analysisFailed(file);
            }
        };
    }
}
