/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.Objects;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Holds the key creation method until we move it to the MetricKey interface.
 *
 * @author Clément Fournier
 */
public class MetricKeyUtil {

    private MetricKeyUtil() {

    }

    /**
     * Creates a new metric key holding a metric which can be computed on a class.
     *
     * TODO:cf Move that to the MetricKey interface once we upgrade the compiler
     *
     * @param metric The metric to use
     * @param name   The name of the metric
     *
     * @return The metric key
     */
    public static <T extends Node> MetricKey<T> of(final Metric<T> metric, final String name) {
        return new MetricKey<T>() {
            @Override
            public String name() {
                return name;
            }


            @Override
            public Metric<T> getCalculator() {
                return metric;
            }


            @Override
            public boolean supports(T node) {
                return metric.supports(node);
            }


            @Override
            public boolean equals(Object obj) {
                return obj != null && getClass() == obj.getClass()
                    && Objects.equals(name(), ((MetricKey) obj).name())
                    && Objects.equals(getCalculator(), ((MetricKey) obj).getCalculator());
            }


            @Override
            public int hashCode() {
                return (metric != null ? metric.hashCode() * 31 : 0) + (name != null ? name.hashCode() : 0);
            }
        };
    }
}
