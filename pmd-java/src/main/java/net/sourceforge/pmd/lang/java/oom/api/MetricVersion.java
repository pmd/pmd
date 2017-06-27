/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.api;

/**
 * Version of a metric. Only one version can be active on a metric. Versions should typically be defined in an enum
 * named 'Version' nested inside the implementation class of the metric. Versions <i>cannot</i> be shared between
 * metrics, otherwise the internal memoization logic breaks down.
 *
 * TODO:cf Strictly enforce the non-sharing of versions, eg through generics.
 *
 * @author Clément Fournier
 */
public interface MetricVersion {

    /**
     * Returns the name of the version.
     *
     * @return The name of the version.
     */
    String name();
}
