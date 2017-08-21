/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import java.util.Map;

import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.metrics.impl.NcssMetric.NcssOption;
import net.sourceforge.pmd.lang.metrics.MetricOption;

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
    protected Map<String, MetricOption> optionMappings() {
        Map<String, MetricOption> mappings = super.optionMappings();
        mappings.put(NcssOption.COUNT_IMPORTS.valueName(), NcssOption.COUNT_IMPORTS);
        return mappings;
    }
}
