/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.QualifiableNode;
import net.sourceforge.pmd.lang.ast.SignedNode;
import net.sourceforge.pmd.lang.metrics.api.MetricKey;
import net.sourceforge.pmd.lang.metrics.api.MetricVersion;
import net.sourceforge.pmd.lang.metrics.api.ResultOption;

/**
 * Base class for metrics computers. These objects compute a metric and memoize it.
 *
 * @param <T> Type of type declaration nodes of the language
 * @param <O> Type of operation declaration nodes of the language
 *
 * @author Clément Fournier
 */
public abstract class AbstractMetricsComputer<T extends QualifiableNode, O extends SignedNode<O> & QualifiableNode> {

    /**
     * Computes the value of a metric for a class and stores the result in the ClassStats object.
     *
     * @param key      The class metric to compute
     * @param node     The AST node of the class
     * @param force    Force the recomputation; if unset, we'll first check for a memoized result
     * @param version  The version of the metric to compute
     * @param memoizer The object memoizing the results
     *
     * @return The result of the computation, or {@code Double.NaN} if it couldn't be performed
     */
    public double computeForType(MetricKey<T> key, T node, boolean force,
                                 MetricVersion version, MetricMemoizer<T> memoizer) {

        ParameterizedMetricKey<T> paramKey = ParameterizedMetricKey.getInstance(key, version);
        // if memo.get(key) == null then the metric has never been computed. NaN is a valid value.
        Double prev = memoizer.getMemo(paramKey);
        if (!force && prev != null) {
            return prev;
        }

        double val = key.getCalculator().computeFor(node, version);
        memoizer.memoize(paramKey, val);

        return val;
    }


    /**
     * Computes the value of a metric for an operation and stores the result in the OperationStats object.
     *
     * @param key      The operation metric to compute
     * @param node     The AST node of the operation
     * @param force    Force the recomputation; if unset, we'll first check for a memoized result
     * @param version  The version of the metric to compute
     * @param memoizer The object memoizing the results
     *
     * @return The result of the computation, or {@code Double.NaN} if it couldn't be performed
     */
    public double computeForOperation(MetricKey<O> key, O node, boolean force,
                                      MetricVersion version, MetricMemoizer<O> memoizer) {

        ParameterizedMetricKey<O> paramKey = ParameterizedMetricKey.getInstance(key, version);
        Double prev = memoizer.getMemo(paramKey);
        if (!force && prev != null) {
            return prev;
        }

        double val = key.getCalculator().computeFor(node, version);
        memoizer.memoize(paramKey, val);
        return val;
    }


    /**
     * Computes an aggregate result using a ResultOption.
     *
     * @param key     The class metric to compute
     * @param node    The AST node of the class
     * @param force   Force the recomputation; if unset, we'll first check for a memoized result
     * @param version The version of the metric
     * @param option  The type of result to compute
     * @param stats   The ClassStats storing info about the class
     *
     * @return The result of the computation, or {@code Double.NaN} if it couldn't be performed
     */
    public double computeWithResultOption(MetricKey<O> key, T node, boolean force, MetricVersion version,
                                          ResultOption option, ProjectMirror<T, O> stats) {

        List<O> ops = findOperations(node);

        List<Double> values = new ArrayList<>();
        for (O op : ops) {
            if (key.supports(op)) {
                MetricMemoizer<O> opStats = stats.getOperationStats(op.getQualifiedName());
                double val = this.computeForOperation(key, op, force, version, opStats);
                if (val != Double.NaN) {
                    values.add(val);
                }
            }
        }

        // FUTURE use streams to do that when we upgrade the compiler to 1.8
        switch (option) {
        case SUM:
            return sum(values);
        case HIGHEST:
            return highest(values);
        case AVERAGE:
            return average(values);
        default:
            return Double.NaN;
        }
    }


    /**
     * Finds the declaration nodes of all methods or constructors that are declared inside a class.
     *
     * @param node The class in which to look for.
     *
     * @return The list of all operations declared inside the specified class.
     */
    protected abstract List<O> findOperations(T node); // TODO:cf this one is computed every time


    private static double sum(List<Double> values) {
        double sum = 0;
        for (double val : values) {
            sum += val;
        }
        return sum;
    }


    private static double highest(List<Double> values) {
        double highest = Double.NEGATIVE_INFINITY;
        for (double val : values) {
            if (val > highest) {
                highest = val;
            }
        }
        return highest == Double.NEGATIVE_INFINITY ? 0 : highest;
    }


    private static double average(List<Double> values) {
        return sum(values) / values.size();
    }


}
