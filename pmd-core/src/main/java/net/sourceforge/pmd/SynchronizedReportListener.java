/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import net.sourceforge.pmd.stat.Metric;

/**
 * Wraps a report listener in order to synchronize calls to it.
 * @deprecated This is an over-locking listener. Implement your own minimizing synchronization.
 */
@Deprecated
public final class SynchronizedReportListener implements ThreadSafeReportListener {

    private final ReportListener wrapped;

    /**
     * Creates a new {@link SynchronizedReportListener} by wrapping the given
     * report listener.
     *
     * @param listener
     *            the listener to be synchronized
     */
    public SynchronizedReportListener(ReportListener listener) {
        this.wrapped = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void ruleViolationAdded(RuleViolation ruleViolation) {
        wrapped.ruleViolationAdded(ruleViolation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void metricAdded(Metric metric) {
        wrapped.metricAdded(metric);
    }

}
