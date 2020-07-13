/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.Collection;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

public class AvoidInstantiatingObjectsInLoopsRule extends AbstractJavaRule {

    public AvoidInstantiatingObjectsInLoopsRule() {
        addRuleChainVisit(ASTAllocationExpression.class);
    }

    /**
     * This method is used to check whether user instantiates variables
     * which are not assigned to arrays/lists in loops.
     * @param node This is the expression of part of java code to be checked.
     * @param data This is the data to return.
     * @return Object This returns the data passed in. If violation happens, violation is added to data.
     */
    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        if (notInsideLoop(node)) {
            return data;
        }

        if (fourthParentNotThrow(node)
                && fourthParentNotReturn(node)
                && notArrayAssignment(node)
                && notCollectionAccess(node)
                && notBreakFollowing(node)) {
            addViolation(data, node);
        }
        return data;
    }

    private boolean notArrayAssignment(ASTAllocationExpression node) {
        if (node.getNthParent(4) instanceof ASTStatementExpression) {
            ASTPrimaryExpression assignee = node.getNthParent(4).getFirstChildOfType(ASTPrimaryExpression.class);
            ASTPrimarySuffix suffix = assignee.getFirstChildOfType(ASTPrimarySuffix.class);
            return suffix == null || !suffix.isArrayDereference();
        }
        return true;
    }

    private boolean notCollectionAccess(ASTAllocationExpression node) {
        if (node.getNthParent(4) instanceof ASTArgumentList && node.getNthParent(8) instanceof ASTStatementExpression) {
            ASTStatementExpression statement = (ASTStatementExpression) node.getNthParent(8);
            return !TypeHelper.isA(statement, Collection.class);
        }
        return true;
    }

    private boolean notBreakFollowing(ASTAllocationExpression node) {
        ASTBlockStatement blockStatement = node.getFirstParentOfType(ASTBlockStatement.class);
        if (blockStatement != null) {
            ASTBlock block = blockStatement.getFirstParentOfType(ASTBlock.class);
            if (block.getNumChildren() > blockStatement.getIndexInParent() + 1) {
                ASTBlockStatement next = (ASTBlockStatement) block.getChild(blockStatement.getIndexInParent() + 1);
                if (next.getNumChildren() == 1 && next.getChild(0).getNumChildren() == 1) {
                    return !(next.getChild(0).getChild(0) instanceof ASTBreakStatement);
                }
            }
        }
        return true;
    }

    /**
     * This method is used to check whether this expression is a throw statement.
     * @param node This is the expression of part of java code to be checked.
     * @return boolean This returns Whether the fourth parent of node is an instance of throw statement.
     */
    private boolean fourthParentNotThrow(ASTAllocationExpression node) {
        return !(node.getNthParent(4) instanceof ASTThrowStatement);
    }

    /**
     * This method is used to check whether this expression is a return statement.
     * @param node This is the expression of part of java code to be checked.
     * @return boolean This returns Whether the fourth parent of node is an instance of return statement.
     */
    private boolean fourthParentNotReturn(ASTAllocationExpression node) {
        return !(node.getNthParent(4) instanceof ASTReturnStatement);
    }

    /**
     * This method is used to check whether this expression is not in a loop.
     * @param node This is the expression of part of java code to be checked.
     * @return boolean <code>false</code> if the given node is inside a loop, <code>true</code> otherwise
     */
    private boolean notInsideLoop(ASTAllocationExpression node) {
        Node n = node.getParent();
        while (n != null) {
            if (n instanceof ASTDoStatement || n instanceof ASTWhileStatement || n instanceof ASTForStatement) {
                return false;
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
        return true;
    }
}
