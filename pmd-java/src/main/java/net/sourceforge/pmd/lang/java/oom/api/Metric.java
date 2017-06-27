/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.api;

import net.sourceforge.pmd.lang.java.ast.AccessNode;

/**
 * Umbrella marker interface for metrics.
 *
 * @author Clément Fournier
 */
public interface Metric {


    /**
     * Checks if the metric can be computed on the node.
     *
     * @param node The node to check
     *
     * @return True if the metric can be computed
     */
    boolean supports(AccessNode node);


    /** Default metric versions. */
    enum Version implements MetricVersion {
        /** Standard option, used as a default. */
        STANDARD
    }

}
