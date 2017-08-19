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

    private static final String NULL_KEY_MESSAGE = "The metric key must not be null";
    private static final String NULL_OPTIONS_MESSAGE = "The metric options must not be null";
    private static final String NULL_NODE_MESSAGE = "The node must not be null";


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

        Objects.requireNonNull(key, NULL_KEY_MESSAGE);
        Objects.requireNonNull(options, NULL_OPTIONS_MESSAGE);
        Objects.requireNonNull(node, NULL_NODE_MESSAGE);

        if (!key.supports(node)) {
            return Double.NaN;
        }

        MetricMemoizer<T> memoizer = getLanguageSpecificProjectMemoizer().getClassMemoizer(node.getQualifiedName());

        return memoizer == null ? Double.NaN
                                : getLanguageSpecificComputer().computeForType(key, node, false,
                                                                               options, memoizer);
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

        Objects.requireNonNull(key, NULL_KEY_MESSAGE);
        Objects.requireNonNull(options, NULL_OPTIONS_MESSAGE);
        Objects.requireNonNull(node, NULL_NODE_MESSAGE);


        if (!key.supports(node)) {
            return Double.NaN;
        }

        MetricMemoizer<O> memoizer = getLanguageSpecificProjectMemoizer().getOperationMemoizer(node.getQualifiedName());

        return memoizer == null ? Double.NaN
                                : getLanguageSpecificComputer().computeForOperation(key, node, false,
                                                                                    options, memoizer);

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

        Objects.requireNonNull(key, NULL_KEY_MESSAGE);
        Objects.requireNonNull(options, NULL_OPTIONS_MESSAGE);
        Objects.requireNonNull(node, NULL_NODE_MESSAGE);
        Objects.requireNonNull(resultOption, "The result option must not be null");

        return getLanguageSpecificComputer().computeWithResultOption(key, node, false, options,
                                                                     resultOption, getLanguageSpecificProjectMemoizer());
    }
}
