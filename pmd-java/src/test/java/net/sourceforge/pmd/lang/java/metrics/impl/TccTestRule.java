/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class TccTestRule extends JavaDoubleMetricTestRule {

    public TccTestRule() {
        super(JavaMetrics.TIGHT_CLASS_COHESION);
    }
}
