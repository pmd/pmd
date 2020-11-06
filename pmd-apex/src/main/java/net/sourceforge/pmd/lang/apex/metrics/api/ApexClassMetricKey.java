/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.api;

import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.metrics.impl.ClassCognitiveComplexityMetric;
import net.sourceforge.pmd.lang.apex.metrics.impl.WmcMetric;
import net.sourceforge.pmd.lang.metrics.MetricKey;

/**
 * @author Clément Fournier
 */
public enum ApexClassMetricKey implements MetricKey<ASTUserClassOrInterface<?>> {
    COGNITIVE(new ClassCognitiveComplexityMetric()),
    WMC(new WmcMetric());


    private final ApexClassMetric calculator;


    ApexClassMetricKey(ApexClassMetric m) {
        calculator = m;
    }


    @Override
    public ApexClassMetric getCalculator() {
        return calculator;
    }


    @Override
    public boolean supports(ASTUserClassOrInterface<?> node) {
        return calculator.supports(node);
    }

}
