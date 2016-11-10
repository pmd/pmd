package net.sourceforge.pmd.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.stat.Metric;

public class FileAnalysisCache implements AnalysisCache {

    private static final Logger LOG = Logger.getLogger(FileAnalysisCache.class.getName());
    
    // TODO : Add benchmark info to cache?
    
    private final File cacheFile;
    private final String pmdVersion;
    private final ConcurrentMap<String, AnalysisResult> fileResultsCache;
    private final ConcurrentMap<String, AnalysisResult> updatedResultsCache;
    
    private long rulesetChecksum;
    private long classpathChecksum;

    /**
     * Creates a new empty cache for the given PMD version
     * @param cache The file on which to store analysis cache
     * @param pmdVersion The version of PMD used to generate this cache
     */
    private FileAnalysisCache(final File cache, final String pmdVersion) {
        this.pmdVersion = pmdVersion;
        this.cacheFile = cache;
        fileResultsCache = new ConcurrentHashMap<>();
        updatedResultsCache = new ConcurrentHashMap<>();
    }

    public static FileAnalysisCache fromFile(final File cacheFile) {
        final FileAnalysisCache cache = new FileAnalysisCache(cacheFile, PMD.VERSION);
        
        if (cacheFile.exists()) {
            try (
                final DataInputStream inputStream = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(cacheFile)));
            ) {
                final String cacheVersion = inputStream.readUTF();
                
                if (PMD.VERSION.equals(cacheVersion)) {
                    // Cache seems valid, load the rest
                    
                    // Get checksums
                    cache.rulesetChecksum = inputStream.readLong();
                    cache.classpathChecksum = inputStream.readLong();
                    
                    // Cached results
                    while (inputStream.available() > 0) {
                        final String fileName = inputStream.readUTF();
                        final long checksum = inputStream.readLong();
                        
                        cache.fileResultsCache.put(fileName, new AnalysisResult(checksum));
                    }
                } else {
                    LOG.info("Analysis cache invalidated, PMD version changed.");
                }
            } catch (final IOException e) {
                // TODO : Handle this!
                LOG.severe("Could not load analysis cache to file.");
            }
        }
        
        return cache;
    }

    @Override
    public void persist() {
        try (
            final DataOutputStream outputStream = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(cacheFile)));
        ) {
            outputStream.writeUTF(pmdVersion);
            
            outputStream.writeLong(rulesetChecksum);
            outputStream.writeLong(classpathChecksum);
            
            for (final Map.Entry<String, AnalysisResult> resultEntry : updatedResultsCache.entrySet()) {
                // TODO : In the future, we want to persist all violations, for now, just store files with NO violations
                if (resultEntry.getValue().getViolations().isEmpty()) {
                    outputStream.writeUTF(resultEntry.getKey());
                    outputStream.writeLong(resultEntry.getValue().getFileChecksum());
                }
            }
        } catch (final IOException e) {
            // TODO : Handle this!
            LOG.severe("Could not store analysis cache to file.");
        }
    }

    // TODO : In the future we may want to return the List<RuleViolation> to be directly added to the report
    @Override
    public boolean isUpToDate(final File sourceFile) {
        // There is a new file being analyzed, prepare entry in updated cache
        final AnalysisResult updatedResult = new AnalysisResult(sourceFile);
        updatedResultsCache.put(sourceFile.getPath(), updatedResult);
        
        // Now check the old cache
        final AnalysisResult analysisResult = fileResultsCache.get(sourceFile.getPath());
        
        if (analysisResult == null) {
            // new file, need to analyze it
            return false;
        }
        
        return analysisResult.getFileChecksum() == updatedResult.getFileChecksum();
    }

    @Override
    public void analysisFailed(final File sourceFile) {
        updatedResultsCache.remove(sourceFile.getPath());
    }

    @Override
    public void checkValidity(final RuleSets ruleSets, final ClassLoader classLoader) {
        boolean cacheIsValid = true;
        
        if (ruleSets.getChecksum() != rulesetChecksum) {
            cacheIsValid = false;
        }
        
        final long classLoaderChecksum;
        if (classLoader instanceof URLClassLoader) {
            final URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
            classLoaderChecksum = Arrays.hashCode(urlClassLoader.getURLs());
            
            if (cacheIsValid && classLoaderChecksum != classpathChecksum) {
                // Do we even care?
                for (final Rule r : ruleSets.getAllRules()) {
                    if (r.usesDFA() || r.usesTypeResolution()) {
                        cacheIsValid = false;
                        break;
                    }
                }
            }
        } else {
            classLoaderChecksum = 0;
        }
        
        if (!cacheIsValid) {
            // Clear the cache
            fileResultsCache.clear();
        }
        
        // Update the local checksums
        rulesetChecksum = ruleSets.getChecksum();
        classpathChecksum = classLoaderChecksum;
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
