/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.metrics.api.JavaMetrics;

/**
 * @author Clément Fournier
 */
public class LocTestRule extends JavaIntMetricTestRule {

    public LocTestRule() {
        super(JavaMetrics.LINES_OF_CODE);
    }
}
