/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import java.util.Map;

import net.sourceforge.pmd.lang.java.metrics.api.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaMetrics.ClassFanOutOption;
import net.sourceforge.pmd.lang.metrics.MetricOption;

/**
 * @author Andreas Pabst
 */
public class CfoTestRule extends JavaIntMetricTestRule {

    public CfoTestRule() {
        super(JavaMetrics.FAN_OUT);
    }

    @Override
    protected Map<String, MetricOption> optionMappings() {
        Map<String, MetricOption> mappings = super.optionMappings();
        mappings.put(ClassFanOutOption.INCLUDE_JAVA_LANG.valueName(), ClassFanOutOption.INCLUDE_JAVA_LANG);
        return mappings;
    }
}
