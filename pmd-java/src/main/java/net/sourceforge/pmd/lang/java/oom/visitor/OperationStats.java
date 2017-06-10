/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.java.oom.Metrics;
import net.sourceforge.pmd.lang.java.oom.Metrics.OperationMetricKey;

/**
 * Statistics for an operation. Keeps a map of all memoized metrics results.
 *
 * @author Clément Fournier
 */
public class OperationStats {

    private final String name;
    private final Map<OperationMetricKey, Double> memo = new HashMap<>();


    OperationStats(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Finds a memoized metric result for the given metric.
     *
     * @param key The metric for which to check for a result
     *
     * @return The memoized result if it was found, or {@code Double.NaN}
     */
    public double getMemo(Metrics.OperationMetricKey key) {
        return memo.get(key) == null ? Double.NaN : memo.get(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof String) {
            return o != null && o.equals(name);
        }

        OperationStats that = (OperationStats) o;

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
