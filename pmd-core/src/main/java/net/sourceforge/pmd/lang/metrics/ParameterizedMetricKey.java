/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.DataMap.DataKey;

/**
 * Represents a key parameterized with its options. Used to index memoization maps.
 *
 * @param <N> Type of node on which the memoized metric can be computed
 *
 * @author Clément Fournier
 * @since 5.8.0
 * @deprecated Is internal API
 */
@InternalApi
@Deprecated
public final class ParameterizedMetricKey<N extends Node> implements DataKey<ParameterizedMetricKey<N>, Double> {

    private static final ConcurrentMap<ParameterizedMetricKey<?>, ParameterizedMetricKey<?>> POOL = new ConcurrentHashMap<>();

    /** The metric key. */
    public final MetricKey<N> key;
    /** The options of the metric. */
    public final MetricOptions options;


    /** Used internally by the pooler. */
    private ParameterizedMetricKey(MetricKey<N> key, MetricOptions options) {
        this.key = key;
        this.options = options;
    }


    @Override
    public String toString() {
        return "ParameterizedMetricKey{key=" + key.name() + ", options=" + options + '}';
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof ParameterizedMetricKey
            && ((ParameterizedMetricKey) o).key.equals(key)
            && ((ParameterizedMetricKey) o).options.equals(options);
    }


    @Override
    public int hashCode() {
        return 31 * key.hashCode() + options.hashCode();
    }


    /**
     * Builds a parameterized metric key.
     *
     * @param key     The key
     * @param options The options
     * @param <N>     The type of node of the metric key
     *
     * @return An instance of parameterized metric key corresponding to the parameters
     */
    @SuppressWarnings("PMD.SingletonClassReturningNewInstance")
    public static <N extends Node> ParameterizedMetricKey<N> getInstance(MetricKey<N> key, MetricOptions options) {
        // sharing instances allows using DataMap, which uses reference identity
        ParameterizedMetricKey<N> tmp = new ParameterizedMetricKey<>(key, options);
        POOL.putIfAbsent(tmp, tmp);

        @SuppressWarnings("unchecked")
        ParameterizedMetricKey<N> result = (ParameterizedMetricKey<N>) POOL.get(tmp);
        return result;
    }
}
