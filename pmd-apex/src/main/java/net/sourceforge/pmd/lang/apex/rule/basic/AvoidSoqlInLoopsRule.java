/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.basic;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.AbstractOptimizationRule;

public class AvoidSoqlInLoopsRule extends AbstractOptimizationRule {

    @Override
    public Object visit(ASTSoqlExpression node, Object data) {
        if (insideLoop(node) && fourthParentNotThrow(node) && fourthParentNotReturn(node)) {
            addViolation(data, node);
        }
        return data;
    }

    private boolean fourthParentNotThrow(ASTSoqlExpression node) {
        return !(node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTThrowStatement);
    }

    private boolean fourthParentNotReturn(ASTSoqlExpression node) {
        return !(node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTReturnStatement);
    }

    private boolean insideLoop(ASTSoqlExpression node) {
        Node n = node.jjtGetParent();
        
        while (n != null) {
            if (n instanceof ASTDoLoopStatement || n instanceof ASTWhileLoopStatement
                    || n instanceof ASTForLoopStatement || n instanceof ASTForEachStatement) {
                return true;
            } 
            else if (n.jjtGetParent() instanceof ASTForLoopStatement || n.jjtGetParent() instanceof ASTForEachStatement) {
                n = n.jjtGetParent();
            }
            
            n = n.jjtGetParent();
        }
        return false;
    }
}
