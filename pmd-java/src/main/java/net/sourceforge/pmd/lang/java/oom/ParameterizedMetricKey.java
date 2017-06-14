/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.java.oom.Metrics.MetricKey;

/**
 * Represents a key parameterized with its options. Used to index memoization maps.
 *
 * @author Clément Fournier
 */
public class ParameterizedMetricKey {

    private static final Map<Integer, ParameterizedMetricKey> POOL = new HashMap<>();

    public final MetricKey key;
    public final MetricOption[] options;

    /** Used internally by the pooler. */
    private ParameterizedMetricKey(MetricKey key, MetricOption[] options) {
        this.key = key;
        this.options = options;
    }

    /** Builds a parameterized metric key. */
    public static ParameterizedMetricKey build(MetricKey key, MetricOption[] options) {
        int code = code(key, options);
        ParameterizedMetricKey paramKey = POOL.get(code);
        if (paramKey == null) {
            POOL.put(code, new ParameterizedMetricKey(key, options));
        }
        return POOL.get(code);
    }

    /** Used by the pooler. */
    private static int code(MetricKey key, MetricOption[] options) {
        int result = key.hashCode();
        result = 31 * result + Arrays.hashCode(options);
        return result;
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
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(options, that.options);
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + Arrays.hashCode(options);
        return result;
    }

    @Override
    public String toString() {
        return "ParameterizedMetricKey{"
            + "key=" + key
            + ", options=" + Arrays.toString(options)
            + '}';
    }
}
