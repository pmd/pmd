/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTDoStatement;
import net.sourceforge.pmd.ast.ASTForInit;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTThrowStatement;
import net.sourceforge.pmd.ast.ASTWhileStatement;
import net.sourceforge.pmd.ast.Node;

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
        Node n = node.jjtGetParent();
        while (n != null) {
            if (n instanceof ASTDoStatement ||
                    n instanceof ASTWhileStatement ||
                    n instanceof ASTForStatement) {
                return true;
            } else if (n instanceof ASTForInit) {
                /*
                 * init part is not technically inside the loop.
                 * Skip parent ASTForStatement but continue higher
                 * up to detect nested loops
                 */
                n = n.jjtGetParent();
            }
            n = n.jjtGetParent();
        }
        return false;
    }
}
