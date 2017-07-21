/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.api;

import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.metrics.AtfdMetric.AtfdOperationMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.CycloMetric.CycloOperationMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.LocMetric.LocOperationMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.NcssMetric.NcssOperationMetric;

/**
 * Keys identifying standard operation metrics.
 */
public enum OperationMetricKey implements MetricKey<ASTMethodOrConstructorDeclaration> {

    /**
     * Access to Foreign Data.
     *
     * @see net.sourceforge.pmd.lang.java.oom.metrics.AtfdMetric
     */
    ATFD(new AtfdOperationMetric()),

    /**
     * Cyclomatic complexity.
     *
     * @see net.sourceforge.pmd.lang.java.oom.metrics.CycloMetric
     */
    CYCLO(new CycloOperationMetric()),

    /**
     * Non Commenting Source Statements.
     *
     * @see net.sourceforge.pmd.lang.java.oom.metrics.NcssMetric
     */
    NCSS(new NcssOperationMetric()),

    /**
     * Lines of Code.
     *
     * @see net.sourceforge.pmd.lang.java.oom.metrics.LocMetric
     */
    LOC(new LocOperationMetric());

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


    /**
     * Creates a new metric key holding a metric which can be computed on a class.
     *
     * TODO:cf Generify and move to MetricKey after upgrading compiler to 1.8
     *
     * @param metric The metric to use
     * @param name   The name of the metric
     *
     * @return The metric key
     */
    public static MetricKey<ASTMethodOrConstructorDeclaration> of(final Metric<ASTMethodOrConstructorDeclaration> metric, final String name) {
        return new MetricKey<ASTMethodOrConstructorDeclaration>() {
            @Override
            public String name() {
                return name;
            }


            @Override
            public Metric<ASTMethodOrConstructorDeclaration> getCalculator() {
                return metric;
            }


            @Override
            public boolean supports(ASTMethodOrConstructorDeclaration node) {
                return metric.supports(node);
            }
        };
    }


}
