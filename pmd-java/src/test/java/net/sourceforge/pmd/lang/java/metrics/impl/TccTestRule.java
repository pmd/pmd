/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.metrics.api.JavaMetrics;
import net.sourceforge.pmd.test.AbstractMetricTestRule;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class TccTestRule extends AbstractMetricTestRule.OfDouble {

    public TccTestRule() {
        super(JavaMetrics.TIGHT_CLASS_COHESION);
    }
}
