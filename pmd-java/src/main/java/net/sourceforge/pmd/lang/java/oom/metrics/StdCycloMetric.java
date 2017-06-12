/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.oom.AbstractMetric;
import net.sourceforge.pmd.lang.java.oom.ClassMetric;
import net.sourceforge.pmd.lang.java.oom.Metrics.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.OperationMetric;
import net.sourceforge.pmd.lang.java.oom.PackageStats;

/**
 * Standard cyclomatic complexity (McCabe's Cyclomatic Number).
 *
 * <p>Standard rules: +1 for each decision point, including case statements but not
 * including boolean operators unlike CyclomaticComplexityRule.
 *
 * @author Clément Fournier, based on work by Alan Hohn and Donald A. Leckie
 */
public class StdCycloMetric extends AbstractMetric implements OperationMetric, ClassMetric {

    @Override
    public double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder) {
        return sumMetricOnOperations(OperationMetricKey.StdCYCLO, node);
    }

    @Override
    public double computeFor(ASTMethodOrConstructorDeclaration node, PackageStats holder) {
        Accumulator cyclo = (Accumulator) node.jjtAccept(new OperationVisitor(), new Accumulator());
        return cyclo.val;
    }

    /**
     * Keeps track of the number of decision points.
     */
    protected static class Accumulator {
        int val = 1;

        void addDecisionPoint() {
            val++;
        }

        void addDecisionPoints(int x) {
            val += x;
        }
    }

    /**
     * Visitor for a method or constructor declaration.
     */
    protected static class OperationVisitor extends JavaParserVisitorAdapter {

        @Override
        public Object visit(ASTSwitchStatement node, Object data) {
            int childCount = node.jjtGetNumChildren();
            int lastIndex = childCount - 1;

            for (int n = 0; n < lastIndex; n++) {
                Node childNode = node.jjtGetChild(n);
                if (childNode instanceof ASTSwitchLabel) {
                    // default is generally not considered a decision (same as "else")
                    ASTSwitchLabel sl = (ASTSwitchLabel) childNode;
                    if (!sl.isDefault()) {
                        childNode = node.jjtGetChild(n + 1);
                        if (childNode instanceof ASTBlockStatement) {
                            ((Accumulator) data).addDecisionPoint();
                        }
                    }
                }
            }
            super.visit(node, data);
            return data;
        }

        @Override
        public Object visit(ASTConditionalExpression node, Object data) {
            if (node.isTernary()) {
                ((Accumulator) data).addDecisionPoint();
                super.visit(node, data);
            }
            return data;
        }

        @Override
        public Object visit(ASTWhileStatement node, Object data) {
            ((Accumulator) data).addDecisionPoint();
            super.visit(node, data);
            return data;
        }

        @Override
        public Object visit(ASTIfStatement node, Object data) {
            ((Accumulator) data).addDecisionPoint();
            super.visit(node, data);
            return data;
        }

        @Override
        public Object visit(ASTCatchStatement node, Object data) {
            ((Accumulator) data).addDecisionPoint();
            super.visit(node, data);
            return data;
        }

        @Override
        public Object visit(ASTForStatement node, Object data) {
            ((Accumulator) data).addDecisionPoint();
            super.visit(node, data);
            return data;
        }

        @Override
        public Object visit(ASTDoStatement node, Object data) {
            ((Accumulator) data).addDecisionPoint();
            super.visit(node, data);
            return data;
        }
    }
}
