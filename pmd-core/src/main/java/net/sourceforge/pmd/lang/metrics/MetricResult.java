/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Objects;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * A data class pairing a computed metric with its key.
 * This is used in {@link LanguageMetricsProvider#computeAllMetricsFor(Node)}.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
@Experimental
public final class MetricResult {

    private final SimpleEntry<MetricKey<?>, Double> simpleEntry;


    public MetricResult(MetricKey<?> key, Double value) {
        simpleEntry = new SimpleEntry<MetricKey<?>, Double>(key, value);
    }


    MetricResult(Entry<? extends MetricKey<?>, Double> entry) {
        simpleEntry = new SimpleEntry<>(entry);
    }


    public MetricKey<?> getKey() {
        return simpleEntry.getKey();
    }


    public Double getValue() {
        return simpleEntry.getValue();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MetricResult that = (MetricResult) o;
        return Objects.equals(simpleEntry, that.simpleEntry);
    }


    @Override
    public int hashCode() {
        return Objects.hash(simpleEntry);
    }
}
