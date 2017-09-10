/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.stat.Metric;

/**
 * Abstract implementation of the analysis cache. Handles all operations, except for persistence.
 */
public abstract class AbstractAnalysisCache implements AnalysisCache {

    protected static final Logger LOG = Logger.getLogger(AbstractAnalysisCache.class.getName());
    protected final String pmdVersion;
    protected final ConcurrentMap<String, AnalysisResult> fileResultsCache;
    protected final ConcurrentMap<String, AnalysisResult> updatedResultsCache;
    protected long rulesetChecksum;
    protected long classpathChecksum;
    protected final CachedRuleMapper ruleMapper = new CachedRuleMapper();
    
    /**
     * Creates a new empty cache
     */
    public AbstractAnalysisCache() {
        pmdVersion = PMD.VERSION;
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
                        + (analysisResult != null ? "file changed" : "no previous results found"));
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

    @Override
    public void checkValidity(final RuleSets ruleSets, final ClassLoader classLoader) {
        boolean cacheIsValid = true;

        if (ruleSets.getChecksum() != rulesetChecksum) {
            LOG.info("Analysis cache invalidated, rulesets changed.");
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
                        LOG.info("Analysis cache invalidated, classpath changed.");
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
        ruleMapper.initialize(ruleSets);
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
