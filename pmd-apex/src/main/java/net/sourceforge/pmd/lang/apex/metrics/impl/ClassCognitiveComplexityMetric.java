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
 * The sum of the cognitive complexities of all the methods within a class.
 *
 * @author Gwilym Kuiper
 */
public class ClassCognitiveComplexityMetric extends AbstractApexClassMetric {
    @Override
    public double computeFor(ASTUserClassOrInterface<?> node, MetricOptions options) {
        return ApexMetrics.get(ApexOperationMetricKey.COGNITIVE, node, ResultOption.SUM);
    }
}
