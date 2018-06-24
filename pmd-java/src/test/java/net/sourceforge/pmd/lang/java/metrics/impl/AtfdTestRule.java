/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class AtfdTestRule extends AbstractMetricTestRule {

    @Override
    protected JavaClassMetricKey getClassKey() {
        return JavaClassMetricKey.ATFD;
    }


    @Override
    protected JavaOperationMetricKey getOpKey() {
        return JavaOperationMetricKey.ATFD;
    }
}
