/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.metrics.cyclo;

import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.codesize.NPathComplexityRule;

/**
 * @author Clément Fournier
 */
public class CycloPathAwareOperationVisitor extends CycloOperationVisitor {
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
