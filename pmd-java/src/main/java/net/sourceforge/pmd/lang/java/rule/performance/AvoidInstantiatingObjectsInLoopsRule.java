/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.*;

public class AvoidInstantiatingObjectsInLoopsRule extends AbstractOptimizationRule {
    /**
     * This method is used to check whether user instantiates variables
     * which are not assigned in loops.
     * @param node This is the expression of part of java code to be checked.
     * @param data This is the data to return.
     * @return Object This returns the data passed in. If violation happens, violation is added to data.
     */
    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        if (insideLoop(node) && fourthParentNotThrow(node) && fourthParentNotReturn(node)) {
            if (thirdParentNotASTExpression(node) && fourthParentNotASTStatementExpression(node)) {
                addViolation(data, node);
            }
        }
        return data;
    }

    /**
     * This method is used to check whether the instantiated variable is assigned or not.
     * @param node This is the expression of part of java code to be checked.
     * @return boolean This returns Whether the third parent of node is an ASTExpression.
     */
    public boolean thirdParentNotASTExpression(ASTAllocationExpression node) {
        if (node.getParent().getClass().toString().equals(
                "class net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix") &&
            node.getParent().getParent().getClass().toString().equals(
                    "class net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression")) {
            return !node.getParent().getParent().getParent().getClass().toString().equals(
                    "class net.sourceforge.pmd.lang.java.ast.ASTExpression");
        }else {
            return false;
        }
    }

    /**
     * This method is used to check whether the instantiated variable is assigned or not.
     * @param node This is the expression of part of java code to be checked.
     * @return boolean This returns Whether the fourth parent of node is an ASTStatementExpression.
     */
    public boolean fourthParentNotASTStatementExpression(ASTAllocationExpression node) {
        return !node.getParent().getParent().getParent().getClass().toString().equals(
                "class net.sourceforge.pmd.lang.java.ast.ASTStateExpression");
    }

    /**
     * This method is used to check whether this expression is a throw statement.
     * @param node This is the expression of part of java code to be checked.
     * @return boolean This returns Whether the fourth parent of node is an instance of throw statement.
     */
    public boolean fourthParentNotThrow(ASTAllocationExpression node) {
        return !(node.getParent().getParent().getParent().getParent() instanceof ASTThrowStatement);
    }

    /**
     * This method is used to check whether this expression is a return statement.
     * @param node This is the expression of part of java code to be checked.
     * @return boolean This returns Whether the fourth parent of node is an instance of return statement.
     */
    public boolean fourthParentNotReturn(ASTAllocationExpression node) {
        return !(node.getParent().getParent().getParent().getParent() instanceof ASTReturnStatement);
    }

    /**
     * This method is used to check whether this expression is in a loop.
     * @param node This is the expression of part of java code to be checked.
     * @return boolean This returns whether the expression is in a loop such as a switch or a while statement.
     */
    public boolean insideLoop(ASTAllocationExpression node) {
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
