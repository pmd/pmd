/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric.CycloOptions;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.ResultOption;

/**
 * Weighed Method Count. It is the sum of the statical complexity of all operations of a class. We use {@link
 * CycloMetric} to quantify the complexity of a metric. [1]
 *
 * <p>The options that can be used of this metric are defined in {@link CycloOptions}. They have the effect of
 * using that options when computing Cyclo before summing.
 *
 * <p>[1] Lanza. Object-Oriented Metrics in Practice.
 *
 * @author Clément Fournier
 * @since June 2017
 */
public final class WmcMetric extends AbstractJavaClassMetric {

    @Override
    public double computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {
        return JavaMetrics.get(JavaOperationMetricKey.CYCLO, node, options, ResultOption.SUM);
    }

}
