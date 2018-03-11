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
 * @since 6.0.0
 */
public final class MetricKeyUtil {

    private MetricKeyUtil() {

    }


    /**
     * Creates a new metric key from its metric and name.
     *
     * @param name   The name of the metric
     * @param metric The metric to use
     * @param <T>    Type of node the metric can be computed on
     *
     * @return The metric key
     */
    // FUTURE Move that to the MetricKey interface once we upgrade the compiler
    public static <T extends Node> MetricKey<T> of(final String name, final Metric<T> metric) {
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
