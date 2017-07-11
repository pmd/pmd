/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

/**
 * Provides a hook into package-private methods of {@code oom}.
 *
 * @author Clément Fournier
 */
public class MetricsHook {

    private MetricsHook() {

    }


    public static void reset() {
        Metrics.reset();
    }

}
