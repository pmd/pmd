/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.internal.RuleSets;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.RuleViolation;

/**
 * An analysis cache for incremental analysis.
 * Simultaneously manages the old version of the cache,
 * and the new, most up-to-date violation cache.
 */
public interface AnalysisCache {

    /**
     * Persists the updated analysis results on whatever medium is used by the cache.
     */
    void persist() throws IOException;

    /**
     * Checks if a given file is up to date in the cache and can be skipped from analysis.
     * Regardless of the return value of this method, each call adds the parameter to the
     * updated cache, which allows {@link FileAnalysisListener#onRuleViolation(RuleViolation)}
     * to add a rule violation to the file. TODO is this really best behaviour? This side-effects seems counter-intuitive.
     *
     * @param document The file to check in the cache
     * @return True if the cache is a hit, false otherwise
     */
    boolean isUpToDate(TextDocument document);

    /**
     * Retrieves cached violations for the given file. Make sure to call {@link #isUpToDate(TextDocument)} first.
     * @param sourceFile The file to check in the cache
     * @return The list of cached violations.
     */
    List<RuleViolation> getCachedViolations(TextDocument sourceFile);

    /**
     * Notifies the cache that analysis of the given file has failed and should not be cached.
     * @param sourceFile The file whose analysis failed
     */
    void analysisFailed(TextDocument sourceFile);

    /**
     * Checks if the cache is valid for the configured rulesets and class loader.
     * If the provided rulesets and classpath don't match those of the cache, the
     * cache is invalidated. This needs to be called before analysis, as it
     * conditions the good behaviour of {@link #isUpToDate(TextDocument)}.
     *
     * @param ruleSets                The rulesets configured for this analysis.
     * @param auxclassPathClassLoader The class loader for auxclasspath configured for this analysis.
     * @param files                   Set of files in the current analysis. File
     *                                records in the cache are matched to the file
     *                                IDs of these files.
     */
    void checkValidity(RuleSets ruleSets, ClassLoader auxclassPathClassLoader, Collection<? extends TextFile> files);

    /**
     * Returns a listener that will be used like in {@link GlobalAnalysisListener#startFileAnalysis(TextFile)}.
     * This should record violations, and call {@link #analysisFailed(TextDocument)}
     * upon error.
     */
    FileAnalysisListener startFileAnalysis(TextDocument file);

}
