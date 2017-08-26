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
public class TccTestRule extends AbstractMetricTestRule {

    @Override
    protected JavaClassMetricKey getClassKey() {
        return JavaClassMetricKey.TCC;
    }


    @Override
    protected JavaOperationMetricKey getOpKey() {
        return null;
    }
}
