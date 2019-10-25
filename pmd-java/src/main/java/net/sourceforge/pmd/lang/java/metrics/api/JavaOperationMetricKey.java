/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.api;

import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.metrics.impl.AtfdMetric.AtfdOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.ClassFanOutMetric.ClassFanOutOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.LocMetric.LocOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.NcssMetric.NcssOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.NpathMetric;
import net.sourceforge.pmd.lang.metrics.MetricKey;


/**
 * Keys identifying standard operation metrics.
 */
public enum JavaOperationMetricKey implements MetricKey<MethodLikeNode> {

    /**
     * Access to Foreign Data.
     *
     * @see net.sourceforge.pmd.lang.java.metrics.impl.AtfdMetric
     */
    ATFD(new AtfdOperationMetric()),

    /**
     * Cyclomatic complexity.
     *
     * @see CycloMetric
     */
    CYCLO(new CycloMetric()),

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
    NPATH(new NpathMetric()),

    /**
     * ClassFanOut Complexity
     *
     * @see ClassFanOutOperationMetric
     */
    CLASS_FAN_OUT(new ClassFanOutOperationMetric());


    private final JavaOperationMetric calculator;


    JavaOperationMetricKey(JavaOperationMetric m) {
        calculator = m;
    }


    @Override
    public JavaOperationMetric getCalculator() {
        return calculator;
    }


    @Override
    public boolean supports(MethodLikeNode node) {
        return calculator.supports(node);
    }


    /**
     * @see #supports(MethodLikeNode)
     * @deprecated Provided here for backwards binary compatibility with {@link #supports(MethodLikeNode)}.
     *             Please explicitly link your code to that method and recompile your code. Will be remove with 7.0.0
     */
    @Deprecated
    public boolean supports(ASTMethodOrConstructorDeclaration node) {
        return this.supports((MethodLikeNode) node);
    }
}
