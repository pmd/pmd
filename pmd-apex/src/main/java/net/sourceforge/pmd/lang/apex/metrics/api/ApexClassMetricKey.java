/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.api;

import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.metrics.MetricKey;

/**
 * @author Clément Fournier
 */
public enum ApexClassMetricKey implements MetricKey<ASTUserClass> {
    DUMMY(null);


    private final ApexClassMetric calculator;


    ApexClassMetricKey(ApexClassMetric m) {
        calculator = m;
    }


    @Override
    public ApexClassMetric getCalculator() {
        return calculator;
    }


    @Override
    public boolean supports(ASTUserClass node) {
        return calculator.supports(node);
    }

}
