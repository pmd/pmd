/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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


        Objects.requireNonNull(key, NULL_KEY_MESSAGE);
        Objects.requireNonNull(options, NULL_OPTIONS_MESSAGE);
        Objects.requireNonNull(ops, NULL_NODE_MESSAGE);
        Objects.requireNonNull(resultOption, "The result option must not be null");


        List<Double> values = new ArrayList<>();
        for (O op : ops) {
            if (key.supports(op)) {
                double val = computeMetric(key, op, options);
                if (!Double.isNaN(val)) {
                    values.add(val);
                }
            }
        }

        // FUTURE use streams to do that when we upgrade the compiler to 1.8
        switch (resultOption) {
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
     */
    public static <N extends Node> double computeMetric(MetricKey<? super N> key, N node, MetricOptions options) {
        return computeMetric(key, node, options, false);
    }

    /**
     * Computes a metric identified by its code on a node, possibly
     * selecting a variant with the {@code options} parameter.
     *
     * @param key            The key identifying the metric to be computed
     * @param node           The node on which to compute the metric
     * @param options        The options of the metric
     * @param forceRecompute Force recomputation of the result
     *
     * @return The value of the metric, or {@code Double.NaN} if the value couldn't be computed
     */
    public static <N extends Node> double computeMetric(MetricKey<? super N> key, N node, MetricOptions options, boolean forceRecompute) {
        Objects.requireNonNull(key, NULL_KEY_MESSAGE);
        Objects.requireNonNull(options, NULL_OPTIONS_MESSAGE);
        Objects.requireNonNull(node, NULL_NODE_MESSAGE);


        if (!key.supports(node)) {
            return Double.NaN;
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
