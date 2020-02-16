/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Basic implementation of a metric memoizer.
 *
 * @param <N> Type of node on which the memoized metric can be computed
 *
 * @author Clément Fournier
 * @since 6.0.0
 * @deprecated See package description
 */
@Deprecated
public class BasicMetricMemoizer<N extends Node> implements MetricMemoizer<N> {


    private final Map<ParameterizedMetricKey<N>, Double> memo = new HashMap<>();


    @Override
    public Double getMemo(ParameterizedMetricKey<N> key) {
        return memo.get(key);
    }


    @Override
    public void memoize(ParameterizedMetricKey<N> key, double value) {
        memo.put(key, value);
    }
}
