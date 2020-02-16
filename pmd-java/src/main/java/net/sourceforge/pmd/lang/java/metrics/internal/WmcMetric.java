/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.internal;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.metrics.AbstractJavaClassMetric;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;

/**
 * Weighed Method count. See the <a href="https://{pmd.website.baseurl}/pmd_java_metrics_index.html">documentation
 * site</a>.
 *
 * @author Clément Fournier
 * @since June 2017
 */
public final class WmcMetric extends AbstractJavaClassMetric {

    @Override
    public double computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {
        return MetricsUtil.computeStatistics(JavaOperationMetricKey.CYCLO, node.getOperations(), options).getSum();
    }

}
