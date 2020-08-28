/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Node;


/**
 * Language-specific provider for metrics. Knows about all the metrics
 * defined for a language. Can be used e.g. to build GUI applications
 * like the designer, in a language independent way. Accessible through
 * {@link LanguageVersionHandler#getLanguageMetricsProvider()}.
 *
 * Note: this is experimental, ie unstable until 7.0.0, after which it will probably
 * be promoted to a real API.
 *
 * @param <T> Type of type declaration nodes of the language
 * @param <O> Type of operation declaration nodes of the language
 *
 * @author Clément Fournier
 * @since 6.11.0
 */
@Experimental
public interface LanguageMetricsProvider {

    Set<Metric<?, ?>> getMetrics();

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
