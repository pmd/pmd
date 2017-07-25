/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import java.util.Map;

import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.metrics.impl.NcssMetric.NcssVersion;
import net.sourceforge.pmd.lang.metrics.api.MetricVersion;

/**
 * @author Clément Fournier
 */
public class NcssTestRule extends AbstractMetricTestRule {

    @Override
    protected boolean isReportClasses() {
        return false;
    }


    @Override
    protected JavaClassMetricKey getClassKey() {
        return JavaClassMetricKey.NCSS;
    }


    @Override
    protected JavaOperationMetricKey getOpKey() {
        return JavaOperationMetricKey.NCSS;
    }


    @Override
    protected Map<String, MetricVersion> versionMappings() {
        Map<String, MetricVersion> mappings = super.versionMappings();
        mappings.put("javaNcss", NcssVersion.JAVANCSS);
        return mappings;
    }
}
