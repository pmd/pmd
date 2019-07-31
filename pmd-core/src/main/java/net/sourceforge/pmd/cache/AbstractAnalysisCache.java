/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.stat.Metric;

/**
 * Abstract implementation of the analysis cache. Handles all operations, except for persistence.
 *
 * @deprecated This is internal API, will be hidden with 7.0.0
 */
@Deprecated
@InternalApi
public abstract class AbstractAnalysisCache implements AnalysisCache {

    protected static final Logger LOG = Logger.getLogger(AbstractAnalysisCache.class.getName());
    protected final String pmdVersion;
    protected final ConcurrentMap<String, AnalysisResult> fileResultsCache;
    protected final ConcurrentMap<String, AnalysisResult> updatedResultsCache;
    protected final CachedRuleMapper ruleMapper = new CachedRuleMapper();
    protected long rulesetChecksum;
    protected long auxClassPathChecksum;
    protected long executionClassPathChecksum;

    /**
     * Creates a new empty cache
     */
    public AbstractAnalysisCache() {
        pmdVersion = PMDVersion.VERSION;
        fileResultsCache = new ConcurrentHashMap<>();
        updatedResultsCache = new ConcurrentHashMap<>();
    }

    @Override
    public boolean isUpToDate(final File sourceFile) {
        // There is a new file being analyzed, prepare entry in updated cache
        final AnalysisResult updatedResult = new AnalysisResult(sourceFile);
        updatedResultsCache.put(sourceFile.getPath(), updatedResult);

        // Now check the old cache
        final AnalysisResult analysisResult = fileResultsCache.get(sourceFile.getPath());

        // is this a known file? has it changed?
        final boolean result = analysisResult != null
                && analysisResult.getFileChecksum() == updatedResult.getFileChecksum();

        if (LOG.isLoggable(Level.FINE)) {
            if (result) {
                LOG.fine("Incremental Analysis cache HIT");
            } else {
                LOG.fine("Incremental Analysis cache MISS - "
                        + (analysisResult != null ? "file changed" : "no previous result found"));
            }
        }

        return result;
    }

    @Override
    public List<RuleViolation> getCachedViolations(final File sourceFile) {
        final AnalysisResult analysisResult = fileResultsCache.get(sourceFile.getPath());

        if (analysisResult == null) {
            // new file, avoid nulls
            return Collections.emptyList();
        }

        return analysisResult.getViolations();
    }

    @Override
    public void analysisFailed(final File sourceFile) {
        updatedResultsCache.remove(sourceFile.getPath());
    }


    /**
     * Returns true if the cache exists. If so, normal cache validity checks
     * will be performed. Otherwise, the cache is necessarily invalid (e.g. on a first run).
     */
    protected abstract boolean cacheExists();


    @Override
    public void checkValidity(final RuleSets ruleSets, final ClassLoader auxclassPathClassLoader) {
        boolean cacheIsValid = cacheExists();

        if (cacheIsValid && ruleSets.getChecksum() != rulesetChecksum) {
            LOG.info("Analysis cache invalidated, rulesets changed.");
            cacheIsValid = false;
        }

        final long currentAuxClassPathChecksum;
        if (auxclassPathClassLoader instanceof URLClassLoader) {
            // we don't want to close our aux classpath loader - we still need it...
            @SuppressWarnings("PMD.CloseResource")
            final URLClassLoader urlClassLoader = (URLClassLoader) auxclassPathClassLoader;
            currentAuxClassPathChecksum = computeClassPathHash(urlClassLoader.getURLs());

            if (cacheIsValid && currentAuxClassPathChecksum != auxClassPathChecksum) {
                // Do we even care?
                for (final Rule r : ruleSets.getAllRules()) {
                    if (r.isDfa() || r.isTypeResolution()) {
                        LOG.info("Analysis cache invalidated, auxclasspath changed.");
                        cacheIsValid = false;
                        break;
                    }
                }
            }
        } else {
            currentAuxClassPathChecksum = 0;
        }

        final long currentExecutionClassPathChecksum = computeClassPathHash(getClassPathEntries());
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
                String extension = FilenameUtils.getExtension(file.toString());
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
            LOG.log(Level.SEVERE, "Incremental analysis can't check execution classpath contents", e);
            throw new RuntimeException(e);
        }

        return entries.toArray(new URL[0]);
    }

    private long computeClassPathHash(final URL... classpathEntry) {
        final Adler32 adler32 = new Adler32();
        for (final URL url : classpathEntry) {
            try (CheckedInputStream inputStream = new CheckedInputStream(url.openStream(), adler32)) {
                // Just read it, the CheckedInputStream will update the checksum on it's own
                while (IOUtils.skip(inputStream, Long.MAX_VALUE) == Long.MAX_VALUE) {
                    // just loop
                }
            } catch (final FileNotFoundException ignored) {
                LOG.warning("Auxclasspath entry " + url.toString() + " doesn't exist, ignoring it");
            } catch (final IOException e) {
                // Can this even happen?
                LOG.log(Level.SEVERE, "Incremental analysis can't check auxclasspath contents", e);
                throw new RuntimeException(e);
            }
        }
        return adler32.getValue();
    }

    @Override
    public void ruleViolationAdded(final RuleViolation ruleViolation) {
        final AnalysisResult analysisResult = updatedResultsCache.get(ruleViolation.getFilename());

        analysisResult.addViolation(ruleViolation);
    }

    @Override
    public void metricAdded(final Metric metric) {
        // Not interested in metrics
    }

}
