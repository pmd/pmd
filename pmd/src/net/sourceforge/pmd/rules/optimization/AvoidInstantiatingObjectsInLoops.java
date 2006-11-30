/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTDoStatement;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTThrowStatement;
import net.sourceforge.pmd.ast.ASTWhileStatement;

public class AvoidInstantiatingObjectsInLoops extends AbstractOptimizationRule {

    public Object visit(ASTAllocationExpression node, Object data) {
        if (insideLoop(node) && fourthParentNotThrow(node) && fourthParentNotReturn(node)) {
            addViolation(data, node);
        }
        return data;
    }

    private boolean fourthParentNotThrow(ASTAllocationExpression node) {
        return !(node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTThrowStatement);
    }

    private boolean fourthParentNotReturn(ASTAllocationExpression node) {
        return !(node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTReturnStatement);
    }

    private boolean insideLoop(ASTAllocationExpression node) {
        if (node.getFirstParentOfType(ASTDoStatement.class) != null) {
            return true;
        }
        if (node.getFirstParentOfType(ASTWhileStatement.class) != null) {
            return true;
        }
        if (node.getFirstParentOfType(ASTForStatement.class) != null) {
            return true;
        }
        return false;
    }
}
