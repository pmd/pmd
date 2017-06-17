/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.keys;

/**
 * Option that can be passed to a metric. Options should be defined in a class or enum nested inside the implementation
 * class of the metric. Bonus point for using an enum, as the name() method is already defined.
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
