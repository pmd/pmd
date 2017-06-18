/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import net.sourceforge.pmd.lang.java.oom.interfaces.MetricKey;
import net.sourceforge.pmd.lang.java.oom.interfaces.OperationMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.AtfdMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.CycloMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.NcssMetric;

/**
 * Keys identifying operation metrics.
 */
public enum OperationMetricKey implements MetricKey<OperationMetric> {

    /** Access to Foreign Data. */ // TODO:cf add short description here for javadoc hints
    ATFD(new AtfdMetric()),
    /** Cyclometric complexity. */
    CYCLO(new CycloMetric()),
    NCSS(new NcssMetric());

    private final OperationMetric calculator;

    OperationMetricKey(OperationMetric m) {
        calculator = m;
    }

    @Override
    public OperationMetric getCalculator() {
        return calculator;
    }
}
