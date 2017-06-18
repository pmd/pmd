/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.api;

/**
 * Umbrella marker interface for metrics.
 *
 * @author Clément Fournier
 */
public interface Metric {

    /** Default metric version. */
    enum Version implements MetricVersion {
        /** Standard option, used as a default. */
        STANDARD
    }

}
