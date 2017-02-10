/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import java.io.File;

import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.ThreadSafeReportListener;

/**
 * An analysis cache for incremental analysis.
 */
public interface AnalysisCache extends ThreadSafeReportListener {

    /**
     * Persist the analysis results on whatever means is used by the cache
     */
    void persist();

    /**
     * Check if a given file is up to date in the cache and can be skipped from analysis
     * @param sourceFile The file to check in the cache
     * @return True if the cache is a hit, false otherwise
     */
    // TODO : In the future we may want to return the List<RuleViolation> to be directly added to the report
    boolean isUpToDate(File sourceFile);

    /**
     * Notifies the cache that analysis of the given file has failed and should not be cached
     * @param sourceFile The file whose analysis failed
     */
    void analysisFailed(File sourceFile);
    
    /**
     * Checks if the cache is valid for the configured rulesets and class loader.
     * @param ruleSets The rulesets configured for this analysis.
     * @param classLoader The class loader configured for this analysis.
     */
    void checkValidity(RuleSets ruleSets, ClassLoader classLoader);
}
