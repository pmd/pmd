/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.api;

/**
 * Key identifying a metric.
 *
 * @param <T> Type of the metric to identify (ClassMetric or OperationMetric).
 */
public interface MetricKey<T extends Metric> extends MemoKey {

    /**
     * Returns the name of the metric.
     *
     * @return The name of the metric
     */
    String name();

    /**
     * Returns the object used to calculate the metric.
     *
     * @return The calculator
     */
    T getCalculator();

}
