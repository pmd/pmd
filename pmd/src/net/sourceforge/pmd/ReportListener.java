/*
 * User: tom
 * Date: Aug 5, 2002
 * Time: 11:52:20 AM
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.stat.Metric;

public interface ReportListener {
    void ruleViolationAdded(RuleViolation ruleViolation);

    void metricAdded(Metric metric);
}
