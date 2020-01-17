/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.QualifiableNode;


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
public interface LanguageMetricsProvider<T extends QualifiableNode, O extends QualifiableNode> {

    /**
     * Provides a hook to do any initializing before the first file is processed by PMD.
     * This can be used by the metrics implementations to reset the cache.
     */
    void initialize();

    /**
     * Returns a list of all supported type metric keys
     * for the language.
     */
    List<? extends MetricKey<T>> getAvailableTypeMetrics();


    /**
     * Returns a list of all supported operation metric keys
     * for the language.
     */
    List<? extends MetricKey<O>> getAvailableOperationMetrics();


    /**
     * Returns the given node casted to {@link T} if it's of the correct
     * type, otherwise returns null.
     */
    T asTypeNode(Node anyNode);


    /**
     * Returns the given node casted to {@link O} if it's of the correct
     * type, otherwise returns null.
     */
    O asOperationNode(Node anyNode);


    /**
     * Like {@link MetricsComputer#computeForType(MetricKey, QualifiableNode, boolean, MetricOptions, MetricMemoizer)},
     * but performs no memoisation.
     */
    double computeForType(MetricKey<T> key, T node, MetricOptions options);


    /**
     * Like {@link MetricsComputer#computeForOperation(MetricKey, QualifiableNode, boolean, MetricOptions, MetricMemoizer)}
     * but performs no memoisation.
     */
    double computeForOperation(MetricKey<O> key, O node, MetricOptions options);


    /**
     * Like {@link MetricsComputer#computeWithResultOption(MetricKey, QualifiableNode, boolean, MetricOptions, ResultOption, ProjectMemoizer)}
     * but performs no memoisation.
     */
    double computeWithResultOption(MetricKey<O> key, T node, MetricOptions options, ResultOption option);


    /**
     * Computes all metrics available on the given node.
     * The returned results may contain Double.NaN as a value.
     *
     * @param node Node to inspect
     *
     * @return A map of metric key to their result, possibly empty, but with no null value
     */
    Map<MetricKey<?>, Double> computeAllMetricsFor(Node node);
}
