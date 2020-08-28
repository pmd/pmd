/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.DataMap.DataKey;

/**
 * Object computing a metric on a node. Metric objects are stateless, which means that instances of the same
 * metric are all equal.
 *
 * @param <N> Type of nodes the metric can be computed on
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public interface Metric<N extends Node, R extends Number> extends DataKey<Metric<N, R>, R> {

    String name();


    /**
     * Checks if the metric can be computed on the node.
     * TODO this should be turned into supports(Node)
     *
     * @param node The node to check
     *
     * @return True if the metric can be computed
     */
    default boolean supports(Node node) {
        return asN(node) != null;
    }

    default boolean maySupport(Node node) {
        return true;
    }

    @Nullable N asN(Node node);


    /**
     * Actually computes the value of a metric for an AST node.
     *
     * @param node    The node
     * @param options The options of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if it could not be computed.
     */
    R computeFor(N node, MetricOptions options);


    interface MetricTargetSelector<N extends Node> {

        N filterCast(Node node);

        boolean maySupport(Node node);
    }

    /**
     * Creates a new metric key from its metric and name.
     *
     * @param name     The name of the metric
     * @param compute  Implementation for {@link #computeFor(Node, MetricOptions)}
     * @param supports Implementation for {@link #supports(Node)}
     * @param <T>      Type of node the metric can be computed on
     *
     * @return The metric key
     *
     * @throws NullPointerException If either parameter is null
     */
    static <T extends Node, R extends Number> Metric<T, R> of(BiFunction<? super T, MetricOptions, ? extends R> compute,
                                                              Function<Node, ? extends @Nullable T> supports,
                                                              @NonNull String name,
                                                              String... aliases) {
        AssertionUtil.requireParamNotNull("name", name);
        AssertionUtil.requireParamNotNull("compute", compute);
        AssertionUtil.requireParamNotNull("supports", supports);

        return new Metric<T, R>() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public @Nullable T asN(Node node) {
                return supports.apply(node);
            }

            @Override
            public R computeFor(T node, MetricOptions options) {
                return compute.apply(node, options);
            }

        };
    }


    static <N extends Node, R extends Number> @Nullable R compute(Metric<N, R> metric, MetricOptions options, Node node) {
        N n = metric.asN(node);
        if (n != null) {
            return metric.computeFor(n, options);
        }
        return null;
    }

}
