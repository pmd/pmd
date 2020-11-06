/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.metrics.impl.internal.ClassFanOutVisitor;
import net.sourceforge.pmd.lang.metrics.MetricOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;


/**
 * The ClassFanOutComplexity counts the usage of other classes within this class.
 *
 * @author Andreas Pabst
 * @since October 2019
 */
public final class ClassFanOutMetric {

    public enum ClassFanOutOption implements MetricOption {
        /** Whether to include Classes in the java.lang package. */
        INCLUDE_JAVA_LANG("includeJavaLang");

        private final String vName;

        ClassFanOutOption(String valueName) {
            this.vName = valueName;
        }

        @Override
        public String valueName() {
            return vName;
        }
    }

    public static final class ClassFanOutClassMetric extends AbstractJavaClassMetric {

        @Override
        public double computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {
            MutableInt cfo = (MutableInt) node.jjtAccept(new ClassFanOutVisitor(options, node), new MutableInt(0));
            return (double) cfo.getValue();
        }
    }

    public static final class ClassFanOutOperationMetric extends AbstractJavaOperationMetric {

        @Override
        public boolean supports(MethodLikeNode node) {
            return true;
        }

        @Override
        public double computeFor(MethodLikeNode node, MetricOptions options) {
            MutableInt cfo;
            // look at the parent to catch annotations
            if (node.getParent() instanceof ASTClassOrInterfaceBodyDeclaration) {
                ASTClassOrInterfaceBodyDeclaration parent = (ASTClassOrInterfaceBodyDeclaration) node.getParent();
                cfo = (MutableInt) parent.jjtAccept(new ClassFanOutVisitor(options, node), new MutableInt(0));
            } else {
                cfo = (MutableInt) node.jjtAccept(new ClassFanOutVisitor(options, node), new MutableInt(0));
            }

            return (double) cfo.getValue();
        }
    }
}
