/*
 * User: tom
 * Date: Aug 5, 2002
 * Time: 11:52:20 AM
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.stat.Metric;

public interface ReportListener {
    public void ruleViolationAdded(RuleViolation ruleViolation);
    public void metricAdded( Metric metric );
}
