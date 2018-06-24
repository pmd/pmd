/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;


/**
 * Provides a hook into package-private methods of {@code apex.metrics}.
 *
 * @author Clément Fournier
 */
public class ApexMetricsHook {

    private ApexMetricsHook() {

    }


    public static void reset() {
        ApexMetrics.reset();
    }


}
