/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.stat.Metric;

/**
 * Listener to be informed about found violations.
 * Note: Suppressed violations are not reported to this listener.
 */
public interface ReportListener {
    /**
     * A new violation has been found.
     * @param ruleViolation the found violation.
     */
    void ruleViolationAdded(RuleViolation ruleViolation);

    /**
     * A new metric point has been reported.
     * @param metric the metric
     */
    void metricAdded(Metric metric);
}
