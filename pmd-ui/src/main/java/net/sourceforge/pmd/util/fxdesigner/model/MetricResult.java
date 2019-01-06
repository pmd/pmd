/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import net.sourceforge.pmd.lang.metrics.MetricKey;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class MetricResult {

    private final SimpleEntry<MetricKey<?>, Double> simpleEntry;


    public MetricResult(MetricKey<?> key, Double value) {
        simpleEntry = new SimpleEntry<>(key, value);
    }


    MetricResult(Entry<? extends MetricKey<?>, ? extends Double> entry) {
        simpleEntry = new SimpleEntry<>(entry);
    }


    public MetricKey<?> getKey() {
        return simpleEntry.getKey();
    }


    public Double getValue() {
        return simpleEntry.getValue();
    }
}
