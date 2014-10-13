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

    /**
     * Creates a new {@link SynchronizedReportListener} by wrapping the given
     * report listener.
     * 
     * @param listener the listener to be synchronized
     */
    public SynchronizedReportListener(ReportListener listener) {
        this.wrapped = listener;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void ruleViolationAdded(RuleViolation ruleViolation) {
        wrapped.ruleViolationAdded(ruleViolation);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void metricAdded(Metric metric) {
        wrapped.metricAdded(metric);
    }

}
