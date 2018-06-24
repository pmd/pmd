/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

/**
 * Lines of Code. See the <a href="https://{pmd.website.baseurl}/pmd_java_metrics_index.html">documentation site</a>.
 *
 * @author Clément Fournier
 * @see NcssMetric
 * @since June 2017
 */
public final class LocMetric {


    public static final class LocOperationMetric extends AbstractJavaOperationMetric {

        @Override
        public boolean supports(MethodLikeNode node) {
            return true;
        }


        @Override
        public double computeFor(MethodLikeNode node, MetricOptions options) {
            return 1 + node.getEndLine() - node.getBeginLine();
        }
    }

    public static final class LocClassMetric extends AbstractJavaClassMetric {

        @Override
        public boolean supports(ASTAnyTypeDeclaration node) {
            return true;
        }


        @Override
        public double computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {
            return 1 + node.getEndLine() - node.getBeginLine();
        }


    }

}
