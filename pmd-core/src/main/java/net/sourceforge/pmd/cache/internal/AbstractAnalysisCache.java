/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

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
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.internal.RuleSets;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.Report.ProcessingError;
import net.sourceforge.pmd.reporting.RuleViolation;

/**
 * Abstract implementation of the analysis cache. Handles all operations, except for persistence.
 */
abstract class AbstractAnalysisCache implements AnalysisCache {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractAnalysisCache.class);
    protected static final ClasspathFingerprinter FINGERPRINTER = new ClasspathFingerprinter();
    protected final String pmdVersion;
    protected final ConcurrentMap<FileId, AnalysisResult> fileResultsCache = new ConcurrentHashMap<>();
    protected final ConcurrentMap<FileId, AnalysisResult> updatedResultsCache = new ConcurrentHashMap<>();
    protected final CachedRuleMapper ruleMapper = new CachedRuleMapper();
    protected long rulesetChecksum;
    protected long auxClassPathChecksum;
    protected long executionClassPathChecksum;

    /**
     * Creates a new empty cache
     */
    AbstractAnalysisCache() {
        pmdVersion = PMDVersion.VERSION;
    }

    @Override
    public boolean isUpToDate(final TextDocument document) {
        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.ANALYSIS_CACHE, "up-to-date check")) {
            final AnalysisResult cachedResult = fileResultsCache.get(document.getFileId());
            final AnalysisResult updatedResult;

            // is this a known file? has it changed?
            final boolean upToDate = cachedResult != null
                && cachedResult.getFileChecksum() == document.getCheckSum();

            if (upToDate) {
                LOG.trace("Incremental Analysis cache HIT");
                
                // copy results over
                updatedResult = cachedResult;
            } else {
                LOG.trace("Incremental Analysis cache MISS - {}",
                          cachedResult != null ? "file changed" : "no previous result found");
                
                // New file being analyzed, create new empty entry
                updatedResult = new AnalysisResult(document.getCheckSum(), new ArrayList<>());
            }

            updatedResultsCache.put(document.getFileId(), updatedResult);
            
            return upToDate;
        }
    }

    @Override
    public List<RuleViolation> getCachedViolations(final TextDocument sourceFile) {
        final AnalysisResult analysisResult = fileResultsCache.get(sourceFile.getFileId());

        if (analysisResult == null) {
            // new file, avoid nulls
            return Collections.emptyList();
        }

        return analysisResult.getViolations();
    }

    @Override
    public void analysisFailed(final TextDocument sourceFile) {
        updatedResultsCache.remove(sourceFile.getFileId());
    }


    /**
     * Returns true if the cache exists. If so, normal cache validity checks
     * will be performed. Otherwise, the cache is necessarily invalid (e.g. on a first run).
     */
    protected abstract boolean cacheExists();


    @Override
    public void checkValidity(RuleSets ruleSets, ClassLoader auxclassPathClassLoader, Collection<? extends TextFile> files) {
        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.ANALYSIS_CACHE, "validity check")) {
            boolean cacheIsValid = cacheExists();

            if (cacheIsValid && ruleSets.getChecksum() != rulesetChecksum) {
                LOG.debug("Analysis cache invalidated, rulesets changed.");
                cacheIsValid = false;
            }

            final long currentAuxClassPathChecksum;
            if (auxclassPathClassLoader instanceof URLClassLoader) {
                // we don't want to close our aux classpath loader - we still need it...
                @SuppressWarnings("PMD.CloseResource") final URLClassLoader urlClassLoader = (URLClassLoader) auxclassPathClassLoader;
                currentAuxClassPathChecksum = FINGERPRINTER.fingerprint(urlClassLoader.getURLs());

                if (cacheIsValid && currentAuxClassPathChecksum != auxClassPathChecksum) {
                    // TODO some rules don't need that (in fact, some languages)
                    LOG.debug("Analysis cache invalidated, auxclasspath changed.");
                    cacheIsValid = false;
                }
            } else {
                currentAuxClassPathChecksum = 0;
            }

            final long currentExecutionClassPathChecksum = FINGERPRINTER.fingerprint(getClassPathEntries());
            if (cacheIsValid && currentExecutionClassPathChecksum != executionClassPathChecksum) {
                LOG.debug("Analysis cache invalidated, execution classpath changed.");
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
        final FileId fileName = file.getFileId();

        return new FileAnalysisListener() {
            private boolean failed = false;

            @Override
            public void onRuleViolation(RuleViolation violation) {
                if (!failed) {
                    updatedResultsCache.get(fileName).addViolation(violation);
                }
            }

            @Override
            public void onError(ProcessingError error) {
                failed = true;
                analysisFailed(file);
            }
        };
    }
}
