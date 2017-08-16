/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

/**
 * Option to pass to a metric. This is use to combine several behaviours together.
 *
 * @author Clément Fournier
 */
public interface MetricOption {

    /**
     * Returns the name of the option.
     *
     * @return The name of the option.
     */
    String name();
}
