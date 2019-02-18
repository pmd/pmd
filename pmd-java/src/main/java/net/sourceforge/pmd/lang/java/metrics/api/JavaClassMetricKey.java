/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.api;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.metrics.internal.AtfdMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.AtfdMetric.AtfdClassMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.LocMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.LocMetric.LocClassMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.NcssMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.NcssMetric.NcssClassMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.NoamMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.NopaMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.TccMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.WmcMetric;
import net.sourceforge.pmd.lang.java.metrics.internal.WocMetric;
import net.sourceforge.pmd.lang.metrics.MetricKey;

/**
 * Keys identifying standard class metrics.
 */
public enum JavaClassMetricKey implements MetricKey<ASTAnyTypeDeclaration> {

    /**
     * Access to Foreign Data.
     *
     * @see AtfdMetric
     */
    ATFD(new AtfdClassMetric()),

    /**
     * Weighed Method Count.
     *
     * @see WmcMetric
     */
    WMC(new WmcMetric()),

    /**
     * Non Commenting Source Statements.
     *
     * @see NcssMetric
     */
    NCSS(new NcssClassMetric()),

    /**
     * Lines of Code.
     *
     * @see LocMetric
     */
    LOC(new LocClassMetric()),

    /**
     * Number of Public Attributes.
     *
     * @see NopaMetric
     */
    NOPA(new NopaMetric()),

    /**
     * Number of Accessor Methods.
     *
     * @see NopaMetric
     */
    NOAM(new NoamMetric()),

    /**
     * Weight of class.
     *
     * @see WocMetric
     */
    WOC(new WocMetric()),

    /**
     * Tight Class Cohesion.
     *
     * @see TccMetric
     */
    TCC(new TccMetric());


    private final JavaClassMetric calculator;


    JavaClassMetricKey(JavaClassMetric m) {
        calculator = m;
    }


    @Override
    public JavaClassMetric getCalculator() {
        return calculator;
    }


    @Override
    public boolean supports(ASTAnyTypeDeclaration node) {
        return calculator.supports(node);
    }

}
