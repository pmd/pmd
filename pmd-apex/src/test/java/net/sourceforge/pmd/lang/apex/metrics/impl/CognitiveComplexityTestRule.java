/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import net.sourceforge.pmd.lang.apex.metrics.api.ApexClassMetricKey;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexOperationMetricKey;

/**
 * @author Gwilym Kuiper
 */
public class CognitiveComplexityTestRule extends AbstractApexMetricTestRule {
    @Override
    protected ApexClassMetricKey getClassKey() {
        return null;
    }

    @Override
    protected ApexOperationMetricKey getOpKey() {
        return ApexOperationMetricKey.COGNITIVE;
    }
}
