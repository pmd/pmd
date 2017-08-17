/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.Objects;

import net.sourceforge.pmd.lang.ast.QualifiableNode;

/**
 * Base class for a façade that can compute metrics for types, operations and compute aggregate results with a result
 * option.
 *
 * @param <T> Type of type declaration nodes of the language
 * @param <O> Type of operation declaration nodes of the language
 *
 * @author Clément Fournier
 */
public abstract class AbstractMetricsFacade<T extends QualifiableNode, O extends QualifiableNode> {


    /**
     * Gets the language specific metrics computer.
     *
     * @return The metrics computer
     */
    protected abstract MetricsComputer<T, O> getLanguageSpecificComputer();


    /**
     * Gets the language-specific project memoizer.
     *
     * @return The project memoizer
     */
    protected abstract ProjectMemoizer<T, O> getLanguageSpecificProjectMemoizer();


    /**
     * Computes a metric identified by its code on a class AST node, possibly selecting a variant with the {@code
     * MetricOptions} parameter.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param options The options of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public double computeForType(MetricKey<T> key, T node, MetricOptions options) {

        Objects.requireNonNull(key, "The metric key must not be null");

        if (!key.supports(node)) {
            return Double.NaN;
        }

        MetricOptions safeOptions = options == null ? MetricOptions.emptyOptions() : options;
        MetricMemoizer<T> memoizer = getLanguageSpecificProjectMemoizer().getClassMemoizer(node.getQualifiedName());

        return memoizer == null ? Double.NaN
                                : getLanguageSpecificComputer().computeForType(key, node, false,
                                                                               safeOptions, memoizer);
    }


    /**
     * Computes a metric identified by its key on a operation AST node.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param options The options of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public double computeForOperation(MetricKey<O> key, O node, MetricOptions options) {

        Objects.requireNonNull(key, "The metric key must not be null");

        if (!key.supports(node)) {
            return Double.NaN;
        }

        MetricOptions safeOptions = options == null ? MetricOptions.emptyOptions() : options;
        MetricMemoizer<O> memoizer = getLanguageSpecificProjectMemoizer().getOperationMemoizer(node.getQualifiedName());

        return memoizer == null ? Double.NaN
                                : getLanguageSpecificComputer().computeForOperation(key, node, false,
                                                                                    safeOptions, memoizer);

    }


    /**
     * Compute the sum, average, or highest value of the operation metric on all operations of the class node. The type
     * of operation is specified by the {@link ResultOption} parameter.
     *
     * @param key          The key identifying the metric to be computed
     * @param node         The node on which to compute the metric
     * @param resultOption The result option to use
     * @param options      The options of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed or {@code resultOption}
     * is {@code null}
     */
    public double computeWithResultOption(MetricKey<O> key, T node,
                                          MetricOptions options, ResultOption resultOption) {

        Objects.requireNonNull(key, "The metric key must not be null");
        Objects.requireNonNull(resultOption, "The result option must not be null");

        MetricOptions safeOptions = options == null ? MetricOptions.emptyOptions() : options;

        return getLanguageSpecificComputer().computeWithResultOption(key, node, false, safeOptions,
                                                                     resultOption, getLanguageSpecificProjectMemoizer());
    }
}
