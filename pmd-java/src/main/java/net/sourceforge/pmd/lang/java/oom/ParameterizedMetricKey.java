/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.java.oom.api.Metric;
import net.sourceforge.pmd.lang.java.oom.api.MetricKey;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;

/**
 * Represents a key parameterized with its version. Used to index memoization maps.
 *
 * @author Clément Fournier
 */
public final class ParameterizedMetricKey {

    private static final Map<Integer, ParameterizedMetricKey> POOL = new HashMap<>();

    /** The metric key. */
    public final MetricKey<? extends Metric> key;
    /** The version of the metric. */
    public final MetricVersion version;

    /** Used internally by the pooler. */
    private ParameterizedMetricKey(MetricKey<? extends Metric> key, MetricVersion version) {
        this.key = key;
        this.version = version;
    }

    /** Builds a parameterized metric key. */
    public static ParameterizedMetricKey build(MetricKey<? extends Metric> key, MetricVersion version) {
        int code = code(key, version);
        ParameterizedMetricKey paramKey = POOL.get(code);
        if (paramKey == null) {
            POOL.put(code, new ParameterizedMetricKey(key, version));
        }
        return POOL.get(code);
    }

    /** Used by the pooler. */
    private static int code(MetricKey key, MetricVersion version) {
        int result = key.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }


    @Override
    public String toString() {
        return "ParameterizedMetricKey{"
            + "key=" + key
            + ", version=" + version
            + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ParameterizedMetricKey that = (ParameterizedMetricKey) o;

        if (!key.equals(that.key)) {
            return false;
        }
        return version.equals(that.version);
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }
}
