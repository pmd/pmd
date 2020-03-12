/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Objects capable of memoizing metrics for a specific type of node. A default implementation is provided, see {@link
 * BasicMetricMemoizer}.
 *
 * @param <N> Type of node on which the memoized metric can be computed
 *
 * @author Clément Fournier
 * @since 6.0.0
 * @deprecated See package description
 */
@Deprecated
public interface MetricMemoizer<N extends Node> {


    /**
     * Fetch a memoized result for a metric and options.
     *
     * @param key The metric key parameterized with its options
     *
     * @return The memoized result, or null if it wasn't found
     */
    Double getMemo(ParameterizedMetricKey<N> key);


    /**
     * Memoizes a result for a metric and options.
     *
     * @param key   The metric key parameterized with its options
     * @param value The value to store
     */
    void memoize(ParameterizedMetricKey<N> key, double value);
}
