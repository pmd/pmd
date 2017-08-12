/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.api;

import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.metrics.impl.AtfdMetric.AtfdOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric.CycloOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.LocMetric.LocOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.NcssMetric.NcssOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.NpathMetric;
import net.sourceforge.pmd.lang.metrics.MetricKey;

/**
 * Keys identifying standard operation metrics.
 */
public enum JavaOperationMetricKey implements MetricKey<ASTMethodOrConstructorDeclaration> {

    /**
     * Access to Foreign Data.
     *
     * @see net.sourceforge.pmd.lang.java.metrics.impl.AtfdMetric
     */
    ATFD(new AtfdOperationMetric()),

    /**
     * Cyclomatic complexity.
     *
     * @see net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric
     */
    CYCLO(new CycloOperationMetric()),

    /**
     * Non Commenting Source Statements.
     *
     * @see net.sourceforge.pmd.lang.java.metrics.impl.NcssMetric
     */
    NCSS(new NcssOperationMetric()),

    /**
     * Lines of Code.
     *
     * @see net.sourceforge.pmd.lang.java.metrics.impl.LocMetric
     */
    LOC(new LocOperationMetric()),


    /**
     * N-path complexity.
     *
     * @see NpathMetric
     */
    NPATH(new NpathMetric());


    private final JavaOperationMetric calculator;


    JavaOperationMetricKey(JavaOperationMetric m) {
        calculator = m;
    }


    @Override
    public JavaOperationMetric getCalculator() {
        return calculator;
    }


    @Override
    public boolean supports(ASTMethodOrConstructorDeclaration node) {
        return calculator.supports(node);
    }


}
