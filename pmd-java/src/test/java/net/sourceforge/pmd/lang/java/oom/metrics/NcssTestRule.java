/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import java.util.Map;

import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.metrics.NcssMetric.NcssVersion;

/**
 * @author Clément Fournier
 */
public class NcssTestRule extends AbstractMetricTestRule {

    @Override
    protected boolean isReportClasses() {
        return false;
    }


    @Override
    protected ClassMetricKey getClassKey() {
        return ClassMetricKey.NCSS;
    }


    @Override
    protected OperationMetricKey getOpKey() {
        return OperationMetricKey.NCSS;
    }


    @Override
    protected Map<String, MetricVersion> versionMappings() {
        Map<String, MetricVersion> mappings = super.versionMappings();
        mappings.put("javaNcss", NcssVersion.JAVANCSS);
        return mappings;
    }
}