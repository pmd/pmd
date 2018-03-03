/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;


import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.metrics.impl.visitors.NpathBaseVisitor;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

/**
 * NPath complexity. See the <a href="https://{pmd.website.baseurl}/pmd_java_metrics_index.html">documentation site</a>.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class NpathMetric extends AbstractJavaOperationMetric {

    @Override
    public double computeFor(MethodLikeNode node, MetricOptions options) {
        return (Integer) node.jjtAccept(NpathBaseVisitor.INSTANCE, null);
    }

}
