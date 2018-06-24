/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import java.util.Map;

import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric.CycloOption;
import net.sourceforge.pmd.lang.metrics.MetricOption;

/**
 * Tests cyclo.
 *
 * @author Clément Fournier
 */
public class CycloTestRule extends AbstractMetricTestRule {

    @Override
    protected JavaClassMetricKey getClassKey() {
        return null;
    }


    @Override
    protected JavaOperationMetricKey getOpKey() {
        return JavaOperationMetricKey.CYCLO;
    }


    @Override
    protected Map<String, MetricOption> optionMappings() {
        Map<String, MetricOption> mappings = super.optionMappings();
        mappings.put(CycloOption.IGNORE_BOOLEAN_PATHS.valueName(), CycloOption.IGNORE_BOOLEAN_PATHS);
        mappings.put(CycloOption.CONSIDER_ASSERT.valueName(), CycloOption.CONSIDER_ASSERT);
        return mappings;
    }
}
