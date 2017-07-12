/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.Metric;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;

/**
 * Tests standard cyclo.
 *
 * @author Clément Fournier
 */
public class CycloTestRule extends AbstractMetricTestRule {

    @Override
    protected ClassMetricKey getClassKey() {
        return ClassMetricKey.CYCLO;
    }


    @Override
    protected OperationMetricKey getOpKey() {
        return OperationMetricKey.CYCLO;
    }


    @Override
    protected String[] versionLabels() {
        return new String[] {"standard", "ignoreBooleanPaths"};
    }


    @Override
    protected MetricVersion[] versionValues() {
        return new MetricVersion[] {Metric.Version.STANDARD, CycloMetric.Version.IGNORE_BOOLEAN_PATHS};
    }
}
