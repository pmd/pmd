/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;

/**
 * @author Clément Fournier
 */
public class WmcTestRule extends AbstractMetricTestRule {

    @Override
    protected boolean isReportMethods() {
        return false;
    }

    @Override
    protected ClassMetricKey getClassKey() {
        return ClassMetricKey.WMC;
    }

    @Override
    protected OperationMetricKey getOpKey() {
        return null;
    }
}
