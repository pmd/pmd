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

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.DataMap.DataKey;

/**
 * A named computation that can be carried out on some nodes. Example
 * include complexity metrics, like cyclomatic complexity.
 *
 * <p>Use with {@link MetricsUtil}, for example, in the Java module:
 * <pre>{@code
 *   if (JavaMetrics.CYCLO.supports(node)) {
 *     int cyclo = MetricsUtil.computeMetric(JavaMetrics.CYCLO, node);
 *     ...
 *   }
 * }</pre>
 *
 * <p>Note that the {@code supports} check is necessary (metrics cannot
 * necessarily be computed on any node of the type they support).
 *
 * <p>Metrics support a concept of {@linkplain MetricOption options},
 * which can be passed to {@link Metric#compute(Metric, Node, MetricOptions) compute}
 * or {@link MetricsUtil#computeMetric(Metric, Node, MetricOptions)}.
 *
 * <p>Metric instances are stateless by contract.
 *
 * <p>To implement your own metrics, use the factory method {@link #of(BiFunction, Function, String, String...) of}.
 * Be aware though, that you cannot register a custom metric into a
 * {@link LanguageMetricsProvider}, which means your metric will not be
 * available from XPath.
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
    String displayName();

    /**
     * List of name aliases by which the metric is recognisable. This
     * list includes the {@link #displayName()} of the metric. These are
     * typically an acronym for the display name, or some such mnemonic.
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
     * can be computed on it. Returns null if the node is not supported.
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
     * Factory method for a metric. The returned instance does not override
     * equals/hashcode.
     *
     * @param compute  Implementation for {@link #computeFor(Node, MetricOptions)} (a pure function).
     * @param cast     Implementation for {@link #castIfSupported(Node)} (a pure function).
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
            public String displayName() {
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
     * in situations where {@code N} is unknown. The result is not cached
     * on the node.
     *
     * @param <N>     Type of nodes the metric supports
     * @param <R>     Return type
     * @param metric  Metric
     * @param node    Node
     * @param options Options for the metric
     *
     * @return Null if the node is unsupported, otherwise the result of the metric.
     *
     * @throws NullPointerException if any of the parameters is null
     */
    static <N extends Node, R extends Number> @Nullable R compute(Metric<N, R> metric, Node node, MetricOptions options) {
        N n = metric.castIfSupported(node);
        if (n != null) {
            return metric.computeFor(n, options);
        }
        return null;
    }

}
