/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import net.sourceforge.pmd.lang.java.oom.interfaces.ClassMetric;
import net.sourceforge.pmd.lang.java.oom.interfaces.MetricKey;
import net.sourceforge.pmd.lang.java.oom.metrics.AtfdMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.CycloMetric;
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
    CYCLO(new CycloMetric());

    private final ClassMetric calculator;

    ClassMetricKey(ClassMetric m) {
        calculator = m;
    }

    @Override
    public ClassMetric getCalculator() {
        return calculator;
    }
}
