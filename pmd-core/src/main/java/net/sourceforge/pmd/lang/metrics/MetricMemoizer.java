/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Base class for metric memoizers. These objects memoize metrics of a specific type, see eg ClassStats in the Java
 * framework.
 *
 * @param <N> Type of node on which the memoized metric can be computed
 *
 * @author Clément Fournier
 */
public abstract class MetricMemoizer<N extends Node> {


    private final Map<ParameterizedMetricKey, Double> memo = new HashMap<>();


    Double getMemo(ParameterizedMetricKey<N> key) {
        return memo.get(key);
    }


    void memoize(ParameterizedMetricKey<N> key, double value) {
        memo.put(key, value);
    }
}
