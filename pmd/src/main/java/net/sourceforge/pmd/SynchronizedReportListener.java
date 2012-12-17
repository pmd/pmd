/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.stat.Metric;

/**
 * Wraps a report listener in order to synchronize calls to it.
 */
public final class SynchronizedReportListener implements ReportListener {

    private final ReportListener wrapped;
    
    public SynchronizedReportListener(ReportListener listener) {
	this.wrapped = listener;
    }
    
    public synchronized void ruleViolationAdded(RuleViolation ruleViolation) {
	wrapped.ruleViolationAdded(ruleViolation);
    }

    public synchronized void metricAdded(Metric metric) {
	wrapped.metricAdded(metric);
    }

}
