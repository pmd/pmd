/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.api;

import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.metrics.AtfdMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.CycloMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.LocMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.NcssMetric;

/**
 * Keys identifying standard operation metrics.
 */
public enum OperationMetricKey implements MetricKey<ASTMethodOrConstructorDeclaration> {

    /** Access to Foreign Data. */
    ATFD(new AtfdMetric.OperationMetric()),

    /** Cyclomatic complexity. */
    CYCLO(new CycloMetric.OperationMetric()),

    /** Non Commenting Source Statements. */
    NCSS(new NcssMetric.OperationMetric()),

    /** Lines of Code. */
    LOC(new LocMetric.OperationMetric());

    private final OperationMetric calculator;


    OperationMetricKey(OperationMetric m) {
        calculator = m;
    }


    @Override
    public OperationMetric getCalculator() {
        return calculator;
    }


    @Override
    public boolean supports(ASTMethodOrConstructorDeclaration node) {
        return calculator.supports(node);
    }

}
