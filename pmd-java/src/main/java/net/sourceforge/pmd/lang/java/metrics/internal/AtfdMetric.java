/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.internal;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.metrics.AbstractJavaClassMetric;
import net.sourceforge.pmd.lang.java.metrics.AbstractJavaOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.java.metrics.internal.visitors.AtfdBaseVisitor;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;

/**
 * Access to Foreign Data. Quantifies the number of foreign fields accessed directly or via accessors.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public final class AtfdMetric {


    public static final class AtfdOperationMetric extends AbstractJavaOperationMetric {

        @Override
        public boolean supports(MethodLikeNode node) {
            return node instanceof ASTMethodDeclaration && super.supports(node);
        }


        @Override
        public double computeFor(MethodLikeNode node, MetricOptions options) {
            return ((MutableInt) node.acceptVisitor(new AtfdBaseVisitor(), new MutableInt(0))).getValue();
        }

    }

    public static final class AtfdClassMetric extends AbstractJavaClassMetric {

        @Override
        public double computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {
            // TODO maybe consider code outside methods
            return MetricsUtil.computeStatistics(JavaOperationMetricKey.ATFD, node.getOperations(), options).getSum();
        }


    }


}
