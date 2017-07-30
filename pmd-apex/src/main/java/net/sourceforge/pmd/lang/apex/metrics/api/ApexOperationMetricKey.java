/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.api;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.metrics.MetricKey;

/**
 * @author Clément Fournier
 */
public enum ApexOperationMetricKey implements MetricKey<ASTMethod> {
    ;

    private final ApexOperationMetric calculator;


    ApexOperationMetricKey(ApexOperationMetric m) {
        calculator = m;
    }


    @Override
    public ApexOperationMetric getCalculator() {
        return calculator;
    }


    @Override
    public boolean supports(ASTMethod node) {
        return calculator.supports(node);
    }
}
