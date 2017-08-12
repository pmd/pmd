/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import java.util.Map;

import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric.CycloVersion;
import net.sourceforge.pmd.lang.metrics.MetricVersion;

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
