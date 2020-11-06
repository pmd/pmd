/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.api;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.metrics.impl.AtfdMetric.AtfdClassMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.ClassFanOutMetric.ClassFanOutClassMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.LocMetric.LocClassMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.NcssMetric.NcssClassMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.NoamMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.NopaMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.TccMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.WmcMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.WocMetric;
import net.sourceforge.pmd.lang.metrics.MetricKey;

/**
 * Keys identifying standard class metrics.
 */
public enum JavaClassMetricKey implements MetricKey<ASTAnyTypeDeclaration> {

    /**
     * Access to Foreign Data.
     *
     * @see net.sourceforge.pmd.lang.java.metrics.impl.AtfdMetric
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
     * @see net.sourceforge.pmd.lang.java.metrics.impl.NcssMetric
     */
    NCSS(new NcssClassMetric()),

    /**
     * Lines of Code.
     *
     * @see net.sourceforge.pmd.lang.java.metrics.impl.LocMetric
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
    TCC(new TccMetric()),

    /**
     * ClassFanOut Complexity
     *
     * @see ClassFanOutClassMetric
     */
    CLASS_FAN_OUT(new ClassFanOutClassMetric());


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
