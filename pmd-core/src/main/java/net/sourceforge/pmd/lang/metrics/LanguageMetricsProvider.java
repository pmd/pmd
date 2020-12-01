/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Node;


/**
 * Language-specific provider for metrics. Knows about all the metrics
 * defined for a language. Can be used e.g. to build GUI applications
 * like the designer, in a language independent way. Accessible through
 * {@link LanguageVersionHandler#getLanguageMetricsProvider()}.
 *
 *
 * @author Clément Fournier
 * @since 6.11.0
 */
public interface LanguageMetricsProvider {

    Set<Metric<?, ?>> getMetrics();


    default @Nullable Metric<?, ?> getMetricWithName(String nameIgnoringCase) {
        for (Metric<?, ?> metric : getMetrics()) {
            for (String nameAlias : metric.nameAliases()) {
                if (nameAlias.equalsIgnoreCase(nameIgnoringCase)) {
                    return metric;
                }
            }
        }
        return null;
    }

    /**
     * Computes all metrics available on the given node.
     * The returned results may contain Double.NaN as a value.
     *
     * @param node Node to inspect
     *
     * @return A map of metric key to their result, possibly empty, but with no null value
     */
    default Map<Metric<?, ?>, Number> computeAllMetricsFor(Node node) {
        Map<Metric<?, ?>, Number> results = new HashMap<>();
        for (Metric<?, ?> metric : getMetrics()) {
            @Nullable Number result = Metric.compute(metric, MetricOptions.emptyOptions(), node);
            if (result != null) {
                results.put(metric, result);
            }
        }
        return results;
    }
}
