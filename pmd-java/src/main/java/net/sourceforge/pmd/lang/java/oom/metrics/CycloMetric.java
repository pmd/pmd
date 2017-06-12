/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.oom.AbstractMetric;
import net.sourceforge.pmd.lang.java.oom.ClassMetric;
import net.sourceforge.pmd.lang.java.oom.Metrics.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.OperationMetric;
import net.sourceforge.pmd.lang.java.oom.PackageStats;
import net.sourceforge.pmd.lang.java.oom.metrics.StdCycloMetric.Accumulator;
import net.sourceforge.pmd.lang.java.rule.codesize.NPathComplexityRule;

/**
 * @author Clément Fournier
 */
public class CycloMetric extends AbstractMetric implements OperationMetric, ClassMetric {

    @Override
    public double computeFor(ASTClassOrInterfaceDeclaration node, PackageStats holder) {
        return sumMetricOnOperations(OperationMetricKey.CYCLO, node);
    }

    @Override
    public double computeFor(ASTMethodOrConstructorDeclaration node, PackageStats holder) {
        Accumulator cyclo = (Accumulator) node.jjtAccept(new OperationVisitor(), new Accumulator());
        return cyclo.val;
    }


    protected static class OperationVisitor extends StdCycloMetric.OperationVisitor {
        @Override
        public Object visit(ASTIfStatement node, Object data) {
            super.visit(node, data);

            int boolCompIf = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
            ((Accumulator) data).addDecisionPoints(boolCompIf);
            return data;
        }

        @Override
        public Object visit(ASTForStatement node, Object data) {
            super.visit(node, data);

            int boolCompFor = NPathComplexityRule
                .sumExpressionComplexity(node.getFirstDescendantOfType(ASTExpression.class));
            ((Accumulator) data).addDecisionPoints(boolCompFor);
            return data;
        }

        @Override
        public Object visit(ASTDoStatement node, Object data) {
            super.visit(node, data);

            int boolCompDo = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
            ((Accumulator) data).addDecisionPoints(boolCompDo);
            return data;
        }

        @Override
        public Object visit(ASTSwitchStatement node, Object data) {
            super.visit(node, data);

            int boolCompSwitch = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
            ((Accumulator) data).addDecisionPoints(boolCompSwitch);
            return data;
        }

        @Override
        public Object visit(ASTWhileStatement node, Object data) {
            super.visit(node, data);

            int boolCompWhile = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
            ((Accumulator) data).addDecisionPoints(boolCompWhile);
            return data;
        }

        @Override
        public Object visit(ASTConditionalExpression node, Object data) {
            super.visit(node, data);

            if (node.isTernary()) {
                int boolCompTern = NPathComplexityRule
                    .sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
                ((Accumulator) data).addDecisionPoints(boolCompTern);
            }
            return data;
        }
    }
}
