/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.metrics.api.MetricKey;
import net.sourceforge.pmd.lang.metrics.api.MetricVersion;

/**
 * Represents a key parameterized with its version. Used to index memoization maps.
 *
 * @author Clément Fournier
 */
public final class ParameterizedMetricKey<N extends Node> {

    private static final Map<Integer, ParameterizedMetricKey<?>> POOL = new HashMap<>();

    /** The metric key. */
    public final MetricKey<N> key;
    /** The version of the metric. */
    public final MetricVersion version;


    /** Used internally by the pooler. */
    private ParameterizedMetricKey(MetricKey<N> key, MetricVersion version) {
        this.key = key;
        this.version = version;
    }


    @Override
    public String toString() {
        return "ParameterizedMetricKey{key=" + key.name() + ", version=" + version.name() + '}';
    }


    @Override
    public boolean equals(Object o) {
        return this == o;
    }


    @Override
    public int hashCode() {
        return code(key, version);
    }


    /** Used by the pooler. */
    private static int code(MetricKey key, MetricVersion version) {
        return 31 * key.hashCode() + version.hashCode();
    }


    /**
     * Builds a parameterized metric key.
     *
     * @param key     The key
     * @param version The version
     * @param <N>     The type of node of the metrickey
     *
     * @return An instance of parameterized metric key corresponding to the parameters
     */
    @SuppressWarnings("unchecked")
    public static <N extends Node> ParameterizedMetricKey<N> getInstance(MetricKey<N> key, MetricVersion version) {
        int code = code(key, version);
        if (!POOL.containsKey(code)) {
            POOL.put(code, new ParameterizedMetricKey<>(key, version));
        }

        return (ParameterizedMetricKey<N>) POOL.get(code);
    }
}
