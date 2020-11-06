/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.metrics.impl.internal.NcssVisitor;
import net.sourceforge.pmd.lang.metrics.MetricOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

/**
 * Non-commenting source statements. See the <a href="https://{pmd.website.baseurl}/pmd_java_metrics_index.html">documentation site</a>.
 *
 * @author Clément Fournier
 * @see LocMetric
 * @since June 2017
 */
public final class NcssMetric {


    /** Variants of NCSS. */
    public enum NcssOption implements MetricOption {
        /** Counts import and package statement. This makes the metric JavaNCSS compliant. */
        COUNT_IMPORTS("countImports");

        private final String vName;


        NcssOption(String valueName) {
            this.vName = valueName;
        }


        @Override
        public String valueName() {
            return vName;
        }
    }

    public static final class NcssClassMetric extends AbstractJavaClassMetric {

        @Override
        public boolean supports(ASTAnyTypeDeclaration node) {
            return true;
        }


        @Override
        public double computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {
            MutableInt ncss = (MutableInt) node.jjtAccept(new NcssVisitor(options, node), new MutableInt(0));
            return (double) ncss.getValue();
        }

    }

    public static final class NcssOperationMetric extends AbstractJavaOperationMetric {

        @Override
        public boolean supports(MethodLikeNode node) {
            return true;
        }


        @Override
        public double computeFor(MethodLikeNode node, MetricOptions options) {
            MutableInt ncss = (MutableInt) node.jjtAccept(new NcssVisitor(options, node), new MutableInt(0));
            return (double) ncss.getValue();
        }

    }

}
