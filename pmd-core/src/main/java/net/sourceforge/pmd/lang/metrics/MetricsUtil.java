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

    public static boolean supportsAll(Node node, Metric<?, ?>... metrics) {
        for (Metric<?, ?> metric : metrics) {
            if (!metric.supports(node)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Computes statistics for the results of a metric over a sequence of nodes.
     *
     * @param key The metric to compute
     * @param ops List of nodes for which to compute the metric
     *
     * @return Statistics for the value of the metric over all the nodes
     */
    public static <O extends Node> DoubleSummaryStatistics computeStatistics(Metric<? super O, ?> key, Iterable<? extends O> ops) {
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
    public static <O extends Node> DoubleSummaryStatistics computeStatistics(Metric<? super O, ?> key,
                                                                             Iterable<? extends O> ops,
                                                                             MetricOptions options) {


        Objects.requireNonNull(key, NULL_KEY_MESSAGE);
        Objects.requireNonNull(options, NULL_OPTIONS_MESSAGE);
        Objects.requireNonNull(ops, NULL_NODE_MESSAGE);

        return StreamSupport.stream(ops.spliterator(), false)
                            .filter(key::supports)
                            .collect(Collectors.summarizingDouble(op -> computeMetric(key, op, options).doubleValue()));
    }

    /**
     * Computes a metric identified by its code on a node, with the default options.
     *
     * @param key  The key identifying the metric to be computed
     * @param node The node on which to compute the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static <N extends Node, R extends Number> R computeMetric(Metric<? super N, R> key, N node) {
        return computeMetric(key, node, MetricOptions.emptyOptions());
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
    public static <N extends Node, R extends Number> R computeMetric(Metric<? super N, R> key, N node, MetricOptions options) {
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
    public static <N extends Node, R extends Number> R computeMetric(Metric<? super N, R> key, N node, MetricOptions options, boolean forceRecompute) {
        Objects.requireNonNull(key, NULL_KEY_MESSAGE);
        Objects.requireNonNull(options, NULL_OPTIONS_MESSAGE);
        Objects.requireNonNull(node, NULL_NODE_MESSAGE);


        if (!key.supports(node)) {
            throw new IllegalArgumentException(key + " cannot be computed on " + node);
        }

        ParameterizedMetricKey<? super N, R> paramKey = ParameterizedMetricKey.getInstance(key, options);
        R prev = node.getUserMap().get(paramKey);
        if (!forceRecompute && prev != null) {
            return prev;
        }

        R val = key.computeFor(node, options);
        node.getUserMap().set(paramKey, val);
        return val;
    }

}
