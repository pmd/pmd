/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import java.util.Map;

import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.metrics.CycloMetric.CycloVersion;

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
    protected Map<String, MetricVersion> versionMappings() {
        Map<String, MetricVersion> mappings = super.versionMappings();
        mappings.put("ignoreBooleanPaths", CycloVersion.IGNORE_BOOLEAN_PATHS);
        return mappings;
    }
}
