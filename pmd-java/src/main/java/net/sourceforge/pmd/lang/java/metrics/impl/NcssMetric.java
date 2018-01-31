/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import java.util.Set;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserDecoratedVisitor;
import net.sourceforge.pmd.lang.java.ast.MethodLike;
import net.sourceforge.pmd.lang.java.metrics.impl.visitors.NcssBaseVisitor;
import net.sourceforge.pmd.lang.java.metrics.impl.visitors.NcssCountImportsDecorator;
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
        public double computeFor(ASTAnyTypeDeclaration node, MetricOptions version) {
            Set<MetricOption> options = version.getOptions();
            JavaParserDecoratedVisitor visitor = new JavaParserDecoratedVisitor(NcssBaseVisitor.INSTANCE);

            if (options.contains(NcssOption.COUNT_IMPORTS)) {
                visitor.decorateWith(new NcssCountImportsDecorator());
            }

            // decorate

            MutableInt ncss = (MutableInt) node.jjtAccept(visitor, new MutableInt(0));
            return (double) ncss.getValue();
        }

    }

    public static final class NcssOperationMetric extends AbstractJavaOperationMetric {

        @Override
        public boolean supports(MethodLike node) {
            return true;
        }


        @Override
        public double computeFor(MethodLike node, MetricOptions version) {
            Set<MetricOption> options = version.getOptions();
            JavaParserDecoratedVisitor visitor = new JavaParserDecoratedVisitor(NcssBaseVisitor.INSTANCE);

            if (options.contains(NcssOption.COUNT_IMPORTS)) {
                visitor.decorateWith(new NcssCountImportsDecorator());
            }

            MutableInt ncss = (MutableInt) node.jjtAccept(visitor, new MutableInt(0));
            return (double) ncss.getValue();
        }

    }

}
