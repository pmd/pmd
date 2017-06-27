/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.rule;

import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;
import net.sourceforge.pmd.lang.java.rule.AbstractSimpleJavaMetricsRule;

/**
 * @author Clément Fournier
 */
public final class NcssCountRule extends AbstractSimpleJavaMetricsRule {


    @Override
    protected ClassMetricKey classMetricKey() {
        return ClassMetricKey.NCSS;
    }


    @Override
    protected OperationMetricKey operationMetricKey() {
        return OperationMetricKey.NCSS;
    }
}
