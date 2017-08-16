/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import net.sourceforge.pmd.lang.apex.metrics.api.ApexClassMetricKey;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexOperationMetricKey;

/**
 * @author Clément Fournier
 */
public class WmcTestRule extends AbstractApexMetricTestRule {

    @Override
    protected boolean isReportMethods() {
        return false;
    }


    @Override
    protected ApexClassMetricKey getClassKey() {
        return ApexClassMetricKey.WMC;
    }


    @Override
    protected ApexOperationMetricKey getOpKey() {
        return null;
    }
}
