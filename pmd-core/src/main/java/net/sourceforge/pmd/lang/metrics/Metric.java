/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.DataMap.DataKey;

/**
 * A named computation that can be carried out on some nodes. Example
 * include complexity metrics.
 *
 * @param <N> Type of nodes the metric can be computed on
 * @param <R> Result type of the metric
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public interface Metric<N extends Node, R extends Number> extends DataKey<Metric<N, R>, R> {

    /**
     * The full name of the metric. This is the preferred name for displaying.
     * Avoid using abbreviations.
     */
    String name();

    /**
     * List of name aliases by which the metric is recognisable. This
     * list includes the {@link #name()} of the metric.
     */
    List<String> nameAliases();


    /**
     * Checks if the metric can be computed on the node.
     *
     * @param node The node to check
     *
     * @return True if the metric can be computed
     *
     * @throws NullPointerException If the parameter is null
     */
    default boolean supports(Node node) {
        return castIfSupported(node) != null;
    }

    /**
     * Casts the node to the more specific type {@code <N>} if this metric
     * can be computed on it. Returns null if the node is node supported.
     *
     * @param node An arbitrary node
     *
     * @return The same node, if it is supported
     *
     * @throws NullPointerException If the parameter is null
     */
    @Nullable N castIfSupported(@NonNull Node node);


    /**
     * Computes the value of the metric for the given node. Behavior if
     * the node is unsupported ({@link #castIfSupported(Node)}) is undefined:
     * the method may throw, return null, or return a garbage value. For that
     * reason the node should be tested beforehand.
     *
     * @param node    The node
     * @param options The options of the metric
     *
     * @return The value of the metric, or null if it could not be computed.
     *
     * @throws NullPointerException if either parameter is null
     */
    R computeFor(N node, MetricOptions options);


    /**
     * Factory method for a metric.
     *
     * @param compute  Implementation for {@link #computeFor(Node, MetricOptions)}
     * @param cast     Implementation for {@link #castIfSupported(Node)}
     * @param fullName The full name of the metric
     * @param aliases  Aliases for the name
     * @param <R>      Return type of the metric
     * @param <T>      Type of node the metric can be computed on
     *
     * @return The metric key
     *
     * @throws NullPointerException If either parameter is null
     */
    static <T extends Node, R extends Number> Metric<T, R> of(BiFunction<? super T, MetricOptions, ? extends R> compute,
                                                              Function<Node, ? extends @Nullable T> cast,
                                                              @NonNull String fullName,
                                                              String... aliases) {
        AssertionUtil.requireParamNotNull("compute", compute);
        AssertionUtil.requireParamNotNull("cast", cast);
        AssertionUtil.requireParamNotNull("fullName", fullName);
        AssertionUtil.requireParamNotNull("aliases", aliases);

        List<String> allNames = listOf(fullName, aliases);

        return new Metric<T, R>() {
            @Override
            public String name() {
                return fullName;
            }

            @Override
            public List<String> nameAliases() {
                return allNames;
            }

            @Override
            public @Nullable T castIfSupported(@NonNull Node node) {
                return cast.apply(node);
            }

            @Override
            public R computeFor(T node, MetricOptions options) {
                return compute.apply(node, options);
            }

        };
    }

    /**
     * Compute a metric on an arbitrary node, if possible. This is useful
     * in situations where {@code N} is unknown.
     *
     * @param metric  Metric
     * @param options Options for the metric
     * @param node    Node
     * @param <N>     Type of nodes the metric supports
     * @param <R>     Return type
     *
     * @return Null if the node is unsupported, otherwise the result of the metric.
     *
     * @throws NullPointerException if any of the parameters is null
     */
    static <N extends Node, R extends Number> @Nullable R compute(Metric<N, R> metric, MetricOptions options, Node node) {
        N n = metric.castIfSupported(node);
        if (n != null) {
            return metric.computeFor(n, options);
        }
        return null;
    }

}
