/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.keys;

/**
 * Umbrella marker interface.
 *
 * @author Clément Fournier
 */
public interface Metric {

    /** Default metric option. */
    enum Option implements MetricOption {
        /** Standard option, used as a default. */
        STANDARD
    }

}
