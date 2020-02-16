/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import net.sourceforge.pmd.lang.ast.QualifiableNode;

/**
 * Basic interface for metrics computers that can compute metrics for types, operations and compute aggregate results
 * with a result option. Computers should typically be separated from the storage units (ProjectMemoizer) to split
 * responsibilities.
 *
 * @param <T> Type of type declaration nodes of the language
 * @param <O> Type of operation declaration nodes of the language
 *
 * @author Clément Fournier
 * @since 6.0.0
 * @deprecated See package description
 */
@Deprecated
public interface MetricsComputer<T extends QualifiableNode, O extends QualifiableNode> {


    /**
     * Computes the value of a metric for a class and stores the result in the ClassStats object.
     *
     * @param key      The class metric to compute
     * @param node     The AST node of the class
     * @param force    Force the recomputation; if unset, we'll first check for a memoized result
     * @param options  The options of the metric to compute
     * @param memoizer The object memoizing the results
     *
     * @return The result of the computation, or {@code Double.NaN} if it couldn't be performed
     */
    double computeForType(MetricKey<T> key, T node, boolean force,
                          MetricOptions options, MetricMemoizer<T> memoizer);


    /**
     * Computes the value of a metric for an operation and stores the result in the OperationStats object.
     *
     * @param key      The operation metric to compute
     * @param node     The AST node of the operation
     * @param force    Force the recomputation; if unset, we'll first check for a memoized result
     * @param options  The options of the metric to compute
     * @param memoizer The object memoizing the results
     *
     * @return The result of the computation, or {@code Double.NaN} if it couldn't be performed
     */
    double computeForOperation(MetricKey<O> key, O node, boolean force,
                               MetricOptions options, MetricMemoizer<O> memoizer);


    /**
     * Computes an aggregate result using a ResultOption.
     *
     * @param key     The class metric to compute
     * @param node    The AST node of the class
     * @param force   Force the recomputation; if unset, we'll first check for a memoized result
     * @param options The options of the metric
     * @param option  The type of result to compute
     * @param stats   The ClassStats storing info about the class
     *
     * @return The result of the computation, or {@code Double.NaN} if it couldn't be performed
     */
    double computeWithResultOption(MetricKey<O> key, T node, boolean force, MetricOptions options,
                                   ResultOption option, ProjectMemoizer<T, O> stats);


}
