/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.api.MetricKey;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;


/**
 * Statistics for an operation. Keeps a map of all memoized metrics results.
 *
 * @author Clément Fournier
 */
/* default */ class OperationStats {

    private final String name;
    private final Map<ParameterizedMetricKey, Double> memo = new HashMap<>();


    /* default */ OperationStats(String name) {
        this.name = name;
    }


    /* default */ String getName() {
        return name;
    }


    /**
     * Computes the value of a metric for an operation.
     *
     * @param key   The operation metric for which to find a memoized result
     * @param node  The AST node of the operation
     * @param force Force the recomputation; if unset, we'll first check for a memoized result
     *
     * @return The result of the computation, or {@code Double.NaN} if it couldn't be performed
     */
    /* default */ double compute(MetricKey<ASTMethodOrConstructorDeclaration> key, ASTMethodOrConstructorDeclaration node,
                                 boolean force, MetricVersion version) {

        ParameterizedMetricKey paramKey = ParameterizedMetricKey.getInstance(key, version);
        Double prev = memo.get(paramKey);
        if (!force && prev != null) {
            return prev;
        }

        double val = key.getCalculator().computeFor(node, version);
        memo.put(paramKey, val);
        return val;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OperationStats stats = (OperationStats) o;

        return name != null ? name.equals(stats.name) : stats.name == null;
    }


    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
