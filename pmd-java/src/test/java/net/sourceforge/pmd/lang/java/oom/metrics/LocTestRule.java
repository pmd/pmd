/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;

/**
 * @author Clément Fournier
 */
public class LocTestRule extends AbstractMetricTestRule {

    @Override
    protected ClassMetricKey getClassKey() {
        return ClassMetricKey.LOC;
    }

    @Override
    protected OperationMetricKey getOpKey() {
        return OperationMetricKey.LOC;
    }
}
