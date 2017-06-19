/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.api;

/**
 * Version of a metric. Only one version can be active on a metric. Versions should typically be defined in an enum
 * named 'Version' nested inside the implementation class of the metric.
 *
 * @author Clément Fournier
 */
public interface MetricVersion extends MetricOption {

    /**
     * Returns the name of the version.
     *
     * @return The name of the version.
     */
    String name();
}
