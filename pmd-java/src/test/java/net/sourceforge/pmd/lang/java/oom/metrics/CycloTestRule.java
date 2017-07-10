/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;

/**
 * Tests standard cyclo.
 *
 * @author Clément Fournier
 */
public class CycloTestRule extends AbstractMetricTestRule {

    @Override
    protected ClassMetricKey getClassKey() {
        return ClassMetricKey.CYCLO;
    }

    @Override
    protected OperationMetricKey getOpKey() {
        return OperationMetricKey.CYCLO;
    }

}
