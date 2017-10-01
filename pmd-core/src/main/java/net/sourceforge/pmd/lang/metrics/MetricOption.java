/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

/**
 * Option to pass to a metric. Options modify the behaviour of a metric. You must bundle them into a
 * {@link MetricOptions} to pass them all to a metric.
 *
 * <p>Options <i>must</i> implement hashCode and equals.
 *
 * <p>Combining options together may be done with a decorator pattern. If you use an AST visitor to compute your
 * metric, look at the Cyclo metric in the Java framework for an example (the decorator pattern is not applicable as
 * is on a visitor, a modified version of it has been implemented for the Java framework).
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
