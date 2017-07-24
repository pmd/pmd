/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics.api;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.metrics.api.Metric;

/**
 * Key identifying a metric. Such keys <i>must</i> implement the hashCode method. Enums are well fitted to serve as
 * metric keys.
 *
 * @param <N> Type of nodes the metric can be computed on
 */
public interface MetricKey<N extends Node> {

    /**
     * Returns the name of the metric.
     *
     * @return The name of the metric
     */
    String name();


    /**
     * Returns the object used to calculate the metric.
     *
     * @return The calculator
     */
    Metric<N> getCalculator();


    /**
     * Returns true if the metric held by this key can be computed on this node.
     *
     * @param node The node to test
     *
     * @return Whether or not the metric can be computed on this node
     */
    boolean supports(N node);


}
