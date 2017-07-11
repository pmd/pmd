/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.api;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.oom.metrics.AtfdMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.CycloMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.LocMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.NcssMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.WmcMetric;

/**
 * Keys identifying class metrics.
 */
public enum ClassMetricKey implements MetricKey<ClassMetric> {
    /** Access to Foreign Data. */
    ATFD(new AtfdMetric()),
    /** Weighed Method Count. */
    WMC(new WmcMetric()),
    /** Cyclomatic complexity. */
    CYCLO(new CycloMetric()),
    /** Non Commenting Source Statements. */
    NCSS(new NcssMetric()),
    /** Lines of Code. */
    LOC(new LocMetric());

    private final ClassMetric calculator;


    ClassMetricKey(ClassMetric m) {
        calculator = m;
    }


    @Override
    public ClassMetric getCalculator() {
        return calculator;
    }


    public boolean supports(ASTAnyTypeDeclaration node) {
        return calculator.supports(node);
    }

}
