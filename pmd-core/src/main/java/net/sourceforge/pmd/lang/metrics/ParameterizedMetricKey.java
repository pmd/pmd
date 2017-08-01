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
 * @param <N> Type of node on which the memoized metric can be computed
 *
 * @author Clément Fournier
 */
public final class ParameterizedMetricKey<N extends Node> {

    private static final Map<ParameterizedMetricKey<?>, ParameterizedMetricKey<?>> POOL = new HashMap<>();

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
        return o instanceof ParameterizedMetricKey
            && ((ParameterizedMetricKey) o).key.equals(key)
            && ((ParameterizedMetricKey) o).version.equals(version);
    }


    @Override
    public int hashCode() {
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
    public static <N extends Node> ParameterizedMetricKey<N> getInstance(MetricKey<N> key, MetricVersion version) {
        ParameterizedMetricKey<N> tmp = new ParameterizedMetricKey<>(key, version);
        if (!POOL.containsKey(tmp)) {
            POOL.put(tmp, tmp);
        }

        @SuppressWarnings("unchecked")
        ParameterizedMetricKey<N> result = (ParameterizedMetricKey<N>) POOL.get(tmp);
        return result;
    }
}
