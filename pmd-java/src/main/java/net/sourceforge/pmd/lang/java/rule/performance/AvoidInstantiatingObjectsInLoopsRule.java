/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;

public class AvoidInstantiatingObjectsInLoopsRule extends AbstractOptimizationRule {

    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        if (insideLoop(node) && fourthParentNotThrow(node) && fourthParentNotReturn(node)) {
            addViolation(data, node);
        }
        return data;
    }

    private boolean fourthParentNotThrow(ASTAllocationExpression node) {
        return !(node.getParent().getParent().getParent().getParent() instanceof ASTThrowStatement);
    }

    private boolean fourthParentNotReturn(ASTAllocationExpression node) {
        return !(node.getParent().getParent().getParent().getParent() instanceof ASTReturnStatement);
    }

    private boolean insideLoop(ASTAllocationExpression node) {
        Node n = node.getParent();
        while (n != null) {
            if (n instanceof ASTDoStatement || n instanceof ASTWhileStatement || n instanceof ASTForStatement) {
                return true;
            } else if (n instanceof ASTForInit) {
                /*
                 * init part is not technically inside the loop. Skip parent
                 * ASTForStatement but continue higher up to detect nested loops
                 */
                n = n.getParent();
            } else if (n.getParent() instanceof ASTForStatement && n.getParent().getNumChildren() > 1
                    && n == n.getParent().getChild(1)) {
                // it is the second child of a ForStatement - which means
                // we are dealing with a for-each construct
                // In that case, we can ignore this allocation expression, as
                // the second child
                // is the expression, over which to iterate.
                // Skip this parent but continue higher up
                // to detect nested loops
                n = n.getParent();
            }
            n = n.getParent();
        }
        return false;
    }
}
