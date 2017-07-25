/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;

/**
 * @author Clément Fournier
 */
public class WmcTestRule extends AbstractMetricTestRule {

    @Override
    protected boolean isReportMethods() {
        return false;
    }

    @Override
    protected JavaClassMetricKey getClassKey() {
        return JavaClassMetricKey.WMC;
    }

    @Override
    protected JavaOperationMetricKey getOpKey() {
        return null;
    }
}
