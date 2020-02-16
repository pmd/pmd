/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.DoubleSummaryStatistics;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Utilities to use {@link Metric} instances.
 */
public final class MetricsUtil {

    static final String NULL_KEY_MESSAGE = "The metric key must not be null";
    static final String NULL_OPTIONS_MESSAGE = "The metric options must not be null";
    static final String NULL_NODE_MESSAGE = "The node must not be null";

    private MetricsUtil() {
        // util class
    }

    public static <N extends Node> boolean supportsAll(N node, MetricKey<N>... metrics) {
        for (MetricKey<N> metric : metrics) {
            if (!metric.supports(node)) {
                return false;
            }
        }
        return true;
    }

    public static <O extends Node> double computeAggregate(MetricKey<? super O> key, Iterable<? extends O> ops, ResultOption resultOption) {
        return computeAggregate(key, ops, MetricOptions.emptyOptions(), resultOption);
    }

    /**
     * Computes an aggregate result for a metric, identified with a {@link ResultOption}.
     *
     * @param key          The metric to compute
     * @param ops          List of nodes for which to aggregate the metric
     * @param options      The options of the metric
     * @param resultOption The type of aggregation to perform
     *
     * @return The result of the computation, or {@code Double.NaN} if it couldn't be performed
     */
    public static <O extends Node> double computeAggregate(MetricKey<? super O> key, Iterable<? extends O> ops, MetricOptions options, ResultOption resultOption) {


        Objects.requireNonNull(resultOption, "The result option must not be null");


        DoubleSummaryStatistics stats =
            computeStatistics(key, ops, options);

        // note these operations coalesce Double.NaN
        // (if any value is NaN, the result is NaN)
        switch (resultOption) {
        case SUM:
            return stats.getSum();
        case HIGHEST:
            double max = stats.getMax();
            return max == Double.NEGATIVE_INFINITY ? 0 : max;
        case AVERAGE:
            return stats.getAverage();
        default:
            throw new IllegalArgumentException("Unknown result option " + resultOption);
        }
    }


    /**
     * Computes statistics for the results of a metric over a sequence of nodes.
     *
     * @param key The metric to compute
     * @param ops List of nodes for which to compute the metric
     *
     * @return Statistics for the value of the metric over all the nodes
     */
    public static <O extends Node> DoubleSummaryStatistics computeStatistics(MetricKey<? super O> key, Iterable<? extends O> ops) {
        return computeStatistics(key, ops, MetricOptions.emptyOptions());
    }

    /**
     * Computes statistics for the results of a metric over a sequence of nodes.
     *
     * @param key     The metric to compute
     * @param ops     List of nodes for which to compute the metric
     * @param options The options of the metric
     *
     * @return Statistics for the value of the metric over all the nodes
     */
    public static <O extends Node> DoubleSummaryStatistics computeStatistics(MetricKey<? super O> key,
                                                                             Iterable<? extends O> ops,
                                                                             MetricOptions options) {


        Objects.requireNonNull(key, NULL_KEY_MESSAGE);
        Objects.requireNonNull(options, NULL_OPTIONS_MESSAGE);
        Objects.requireNonNull(ops, NULL_NODE_MESSAGE);

        return StreamSupport.stream(ops.spliterator(), false)
                            .filter(key::supports)
                            .collect(Collectors.summarizingDouble(op -> computeMetric(key, op, options)));
    }

    /**
     * Computes a metric identified by its code on a node, with the default options.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static <N extends Node> double computeMetric(MetricKey<? super N> key, N node) {
        return computeMetric(key, node, MetricOptions.emptyOptions());
    }

    /**
     * Computes a metric identified by its code on a node, possibly
     * selecting a variant with the {@code options} parameter.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param options The options of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     *
     * @deprecated This is provided for compatibility with pre 6.21.0
     *     behavior. Users of a metric should always check beforehand if
     *     the metric supports the argument.
     */
    @Deprecated
    public static <N extends Node> double computeMetricOrNaN(MetricKey<? super N> key, N node, MetricOptions options) {
        if (!key.supports(node)) {
            return Double.NaN;
        }
        return computeMetric(key, node, options, false);
    }

    /**
     * Computes a metric identified by its code on a node, possibly
     * selecting a variant with the {@code options} parameter.
     *
     * <p>Note that contrary to the previous behaviour, this method
     * throws an exception if the metric does not support the node.
     *
     * @param key     The key identifying the metric to be computed
     * @param node    The node on which to compute the metric
     * @param options The options of the metric
     *
     * @return The value of the metric
     *
     * @throws IllegalArgumentException If the metric does not support the given node
     */
    public static <N extends Node> double computeMetric(MetricKey<? super N> key, N node, MetricOptions options) {
        return computeMetric(key, node, options, false);
    }

    /**
     * Computes a metric identified by its code on a node, possibly
     * selecting a variant with the {@code options} parameter.
     *
     * <p>Note that contrary to the previous behaviour, this method
     * throws an exception if the metric does not support the node.
     *
     * @param key            The key identifying the metric to be computed
     * @param node           The node on which to compute the metric
     * @param options        The options of the metric
     * @param forceRecompute Force recomputation of the result
     *
     * @return The value of the metric
     *
     * @throws IllegalArgumentException If the metric does not support the given node
     */
    public static <N extends Node> double computeMetric(MetricKey<? super N> key, N node, MetricOptions options, boolean forceRecompute) {
        Objects.requireNonNull(key, NULL_KEY_MESSAGE);
        Objects.requireNonNull(options, NULL_OPTIONS_MESSAGE);
        Objects.requireNonNull(node, NULL_NODE_MESSAGE);


        if (!key.supports(node)) {
            throw new IllegalArgumentException(key + " cannot be computed on " + node);
        }

        ParameterizedMetricKey<? super N> paramKey = ParameterizedMetricKey.getInstance(key, options);
        Double prev = node.getUserMap().get(paramKey);
        if (!forceRecompute && prev != null) {
            return prev;
        }

        double val = key.getCalculator().computeFor(node, options);
        node.getUserMap().set(paramKey, val);
        return val;
    }

}
