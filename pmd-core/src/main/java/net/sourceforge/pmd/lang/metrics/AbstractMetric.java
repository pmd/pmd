/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Abstract class for all metrics.
 *
 * @param <N> Type of nodes the metric can be computed on
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public abstract class AbstractMetric<N extends Node> implements Metric<N> {

    /**
     * Metrics should be stateless, thus any instance of the same metric class should be equal.
     *
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        return o != null && o.getClass() == this.getClass();
    }


    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

}
