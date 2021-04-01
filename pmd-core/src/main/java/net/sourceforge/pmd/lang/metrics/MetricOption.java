/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

/**
 * Option to pass to a metric. Options modify the behaviour of a metric.
 * You must bundle them into a {@link MetricOptions} to pass them all to a metric.
 *
 * <p>Options must be suitable for use in sets (implement equals/hashcode,
 * or be singletons).
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public interface MetricOption {

    /**
     * Returns the name of the option constant.
     *
     * @return The name of the option constant.
     */
    String name();

    /**
     * Returns the name of the option as it should be used in properties.
     *
     * @return The name of the option.
     */
    String valueName();

}
