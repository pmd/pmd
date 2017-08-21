/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.metrics.ApexMetrics;
import net.sourceforge.pmd.lang.apex.metrics.api.ApexOperationMetricKey;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.ResultOption;

/**
 * See the doc for the Java metric.
 *
 * @author Clément Fournier
 */
public class WmcMetric extends AbstractApexClassMetric {

    @Override
    public double computeFor(ASTUserClassOrInterface<?> node, MetricOptions options) {
        return ApexMetrics.get(ApexOperationMetricKey.CYCLO, node, ResultOption.SUM);
    }
}
