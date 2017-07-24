/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.metrics;

import java.util.Map;

import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.metrics.api.MetricVersion;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.metrics.metrics.CycloMetric.CycloVersion;

/**
 * Tests standard cyclo.
 *
 * @author Clément Fournier
 */
public class CycloTestRule extends AbstractMetricTestRule {

    @Override
    protected JavaClassMetricKey getClassKey() {
        return JavaClassMetricKey.CYCLO;
    }


    @Override
    protected JavaOperationMetricKey getOpKey() {
        return JavaOperationMetricKey.CYCLO;
    }


    @Override
    protected Map<String, MetricVersion> versionMappings() {
        Map<String, MetricVersion> mappings = super.versionMappings();
        mappings.put("ignoreBooleanPaths", CycloVersion.IGNORE_BOOLEAN_PATHS);
        return mappings;
    }
}
