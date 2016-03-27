/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.codesize;

import net.sourceforge.pmd.lang.apex.ast.ASTExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTTernaryExpression;
import net.sourceforge.pmd.lang.apex.rule.codesize.NPathComplexityRule;

/**
 * @author ported from Java version of Donald A. Leckie,
 *
 * @version $Revision: 5956 $, $Date: 2008-04-04 04:59:25 -0500 (Fri, 04 Apr
 *          2008) $
 * @since January 14, 2003
 */
public class CyclomaticComplexityRule extends StdCyclomaticComplexityRule {

    @Override
    public Object visit(ASTIfBlockStatement node, Object data) {
        super.visit(node, data);

        int boolCompIf = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
        entryStack.peek().bumpDecisionPoints(boolCompIf);
        return data;
    }

    @Override
    public Object visit(ASTForEachStatement node, Object data) {
        super.visit(node, data);

        int boolCompFor = NPathComplexityRule
                .sumExpressionComplexity(node.getFirstDescendantOfType(ASTExpression.class));
        entryStack.peek().bumpDecisionPoints(boolCompFor);
        return data;
    }

    @Override
    public Object visit(ASTForLoopStatement node, Object data) {
        super.visit(node, data);

        int boolCompFor = NPathComplexityRule
                .sumExpressionComplexity(node.getFirstDescendantOfType(ASTExpression.class));
        entryStack.peek().bumpDecisionPoints(boolCompFor);
        return data;
    }

    @Override
    public Object visit(ASTWhileLoopStatement node, Object data) {
        super.visit(node, data);

        int boolCompWhile = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
        entryStack.peek().bumpDecisionPoints(boolCompWhile);
        return data;
    }

    @Override
    public Object visit(ASTDoLoopStatement node, Object data) {
        super.visit(node, data);

        int boolCompDo = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
        entryStack.peek().bumpDecisionPoints(boolCompDo);
        return data;
    }

    @Override
    public Object visit(ASTTernaryExpression node, Object data) {
        super.visit(node, data);

        int boolCompWhile = NPathComplexityRule.sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));
        entryStack.peek().bumpDecisionPoints(boolCompWhile);
        return data;
    }
}
