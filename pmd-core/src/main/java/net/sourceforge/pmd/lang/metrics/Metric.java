/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Object computing a metric on a node. Metric objects are stateless, which means that instances of the same
 * metric are all equal.
 *
 * @param <N> Type of nodes the metric can be computed on
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public interface Metric<N extends Node> {


    /**
     * Checks if the metric can be computed on the node.
     *
     * @param node The node to check
     *
     * @return True if the metric can be computed
     */
    boolean supports(N node);


    /**
     * Actually computes the value of a metric for an AST node.
     *
     * @param node    The node
     * @param options The options of the metric
     *
     * @return The value of the metric, or {@code Double.NaN} if it could not be computed.
     */
    double computeFor(N node, MetricOptions options);

}
