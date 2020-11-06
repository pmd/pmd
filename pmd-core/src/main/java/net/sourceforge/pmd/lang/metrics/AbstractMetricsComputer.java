/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.List;

import net.sourceforge.pmd.lang.ast.QualifiableNode;

/**
 * Base class for metrics computers. These objects compute a metric and memoize it.
 *
 * @param <T> Type of type declaration nodes of the language
 * @param <O> Type of operation declaration nodes of the language
 *
 * @author Clément Fournier
 * @since 6.0.0
 * @deprecated See package description
 */
@Deprecated
public abstract class AbstractMetricsComputer<T extends QualifiableNode, O extends QualifiableNode>
    implements MetricsComputer<T, O> {

    @Override
    public double computeForType(MetricKey<T> key, T node, boolean force,
                                 MetricOptions options, MetricMemoizer<T> memoizer) {
        return MetricsUtil.computeMetric(key, node, options);
    }


    @Override
    public double computeForOperation(MetricKey<O> key, O node, boolean force,
                                      MetricOptions options, MetricMemoizer<O> memoizer) {

        return MetricsUtil.computeMetric(key, node, options);
    }


    @Override
    public double computeWithResultOption(MetricKey<O> key, T node, boolean force, MetricOptions options,
                                          ResultOption option, ProjectMemoizer<T, O> stats) {
        return MetricsUtil.computeAggregate(key, findOperations(node), options, option);
    }


    /**
     * Finds the declaration nodes of all methods or constructors that are declared inside a class. This is language
     * specific, as it depends on the AST.
     *
     * @param node The class in which to look for.
     *
     * @return The list of all operations declared inside the specified class.
     */
    protected abstract List<O> findOperations(T node); // TODO:cf this one is computed every time


}
