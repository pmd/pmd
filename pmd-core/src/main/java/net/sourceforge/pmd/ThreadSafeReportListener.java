/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import net.sourceforge.pmd.stat.Metric;

/**
 * Marker interface for report listeners that, being thread-safe, need not
 * extra synchronization.
 *
 * Thread-safety is required only for concurrently notifying about different files.
 * Same file violations are guaranteed to be reported serially.
 */
public interface ThreadSafeReportListener {
    /**
     * A new violation has been found.
     *
     * @param ruleViolation
     *            the found violation.
     */
    void ruleViolationAdded(RuleViolation ruleViolation);

    /**
     * A new metric point has been reported.
     *
     * @param metric
     *            the metric
     * @deprecated see {@link net.sourceforge.pmd.lang.rule.stat.StatisticalRule}
     */
    @Deprecated
    void metricAdded(Metric metric);
}
