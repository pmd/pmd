/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import java.io.File;
import java.util.List;

import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ThreadSafeReportListener;
import net.sourceforge.pmd.annotation.InternalApi;

/**
 * An analysis cache for incremental analysis.
 * Simultaneously manages the old version of the cache,
 * and the new, most up-to-date violation cache.
 *
 * @deprecated This is internal API, will be hidden with 7.0.0
 */
@Deprecated
@InternalApi
public interface AnalysisCache extends ThreadSafeReportListener {

    /**
     * Persists the updated analysis results on whatever medium is used by the cache.
     */
    void persist();

    /**
     * Checks if a given file is up to date in the cache and can be skipped from analysis.
     * Regardless of the return value of this method, each call adds the parameter to the
     * updated cache, which allows {@link #ruleViolationAdded(RuleViolation)} to add a rule
     * violation to the file. TODO is this really best behaviour? This side-effects seems counter-intuitive.
     *
     * @param sourceFile The file to check in the cache
     * @return True if the cache is a hit, false otherwise
     */
    boolean isUpToDate(File sourceFile);

    /**
     * Retrieves cached violations for the given file. Make sure to call {@link #isUpToDate(File)} first.
     * @param sourceFile The file to check in the cache
     * @return The list of cached violations.
     */
    List<RuleViolation> getCachedViolations(File sourceFile);

    /**
     * Notifies the cache that analysis of the given file has failed and should not be cached.
     * @param sourceFile The file whose analysis failed
     */
    void analysisFailed(File sourceFile);

    /**
     * Checks if the cache is valid for the configured rulesets and class loader.
     * If the provided rulesets and classpath don't match those of the cache, the
     * cache is invalidated. This needs to be called before analysis, as it
     * conditions the good behaviour of {@link #isUpToDate(File)}.
     *
     * @param ruleSets The rulesets configured for this analysis.
     * @param auxclassPathClassLoader The class loader for auxclasspath configured for this analysis.
     */
    void checkValidity(RuleSets ruleSets, ClassLoader auxclassPathClassLoader);
}
