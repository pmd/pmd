/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Holds the key creation method until we move it to the MetricKey interface.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
@Deprecated
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
     *
     * @deprecated Use {@link MetricKey#of(String, Metric)}
     */
    @Deprecated
    public static <T extends Node> MetricKey<T> of(final String name, final Metric<T> metric) {
        return MetricKey.of(name, metric);
    }
}
