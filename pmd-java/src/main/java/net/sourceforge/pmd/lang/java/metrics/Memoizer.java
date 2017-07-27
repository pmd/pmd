/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.metrics.ParameterizedMetricKey;

/**
 * @author Clément Fournier
 */
public abstract class Memoizer {


    private final Map<ParameterizedMetricKey, Double> memo = new HashMap<>();


    Double getMemo(ParameterizedMetricKey key) {
        return memo.get(key);
    }


    void memoize(ParameterizedMetricKey key, double value) {
        memo.put(key, value);
    }
}
