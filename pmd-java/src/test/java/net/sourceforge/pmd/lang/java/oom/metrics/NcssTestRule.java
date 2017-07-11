/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;

/**
 * @author Clément Fournier
 */
public class NcssTestRule extends AbstractMetricTestRule {

    @Override
    protected boolean isReportClasses() {
        return false;
    }

    @Override
    protected ClassMetricKey getClassKey() {
        return ClassMetricKey.NCSS;
    }

    @Override
    protected OperationMetricKey getOpKey() {
        return OperationMetricKey.NCSS;
    }
}
