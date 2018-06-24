/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.metrics.AbstractMetric;


/**
 * Base class for metrics. Metric objects encapsulate the computational logic required to compute a metric from a node.
 *
 * @param <N> Type of node the metric can be computed on
 *
 * @author Clément Fournier
 */
public abstract class AbstractJavaMetric<N extends Node> extends AbstractMetric<N> {

}
