/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.Collection;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAccess;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class AvoidInstantiatingObjectsInLoopsRule extends AbstractJavaRulechainRule {

    public AvoidInstantiatingObjectsInLoopsRule() {
        super(ASTConstructorCall.class);
    }

    /**
     * This method is used to check whether user instantiates variables
     * which are not assigned to arrays/lists in loops.
     * @param node This is the expression of part of java code to be checked.
     * @param data This is the data to return.
     * @return Object This returns the data passed in. If violation happens, violation is added to data.
     */
    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        if (notInsideLoop(node)) {
            return data;
        }

        if (notAThrowStatement(node)
                && notAReturnStatement(node)
                && notBreakFollowing(node)
                && notArrayAssignment(node)
                && notCollectionAccess(node)) {
            addViolation(data, node);
        }
        return data;
    }

    private boolean notArrayAssignment(ASTConstructorCall node) {
        if (node.getParent() instanceof ASTAssignmentExpression) {
            if (node.getIndexInParent() == 1) {
                Node assignee = node.getParent().getFirstChild();
                return !(assignee instanceof ASTArrayAccess);
            }
        }
        return true;
    }

    private boolean notCollectionAccess(ASTConstructorCall node) {
        if (node.getParent() instanceof ASTArgumentList && node.getNthParent(2) instanceof ASTMethodCall) {
            ASTMethodCall methodCall = (ASTMethodCall) node.getNthParent(2);
            return !TypeTestUtil.isA(Collection.class, methodCall.getQualifier());
        }
        return true;
    }

    private boolean notBreakFollowing(ASTConstructorCall node) {
        JavaNode statement = node.ancestors().filter(n -> n.getParent() instanceof ASTBlock).first();
        if (statement != null) {
            ASTBlock block = (ASTBlock) statement.getParent();
            if (block.getNumChildren() > statement.getIndexInParent() + 1) {
                ASTStatement next = block.getChild(statement.getIndexInParent() + 1);
                return !(next instanceof ASTBreakStatement);
            }
        }
        return true;
    }

    /**
     * This method is used to check whether this expression is a throw statement.
     * @param node This is the expression of part of java code to be checked.
     * @return boolean This returns whether the given constructor call is part of a throw statement
     */
    private boolean notAThrowStatement(ASTConstructorCall node) {
        return !(node.getParent() instanceof ASTThrowStatement);
    }

    /**
     * This method is used to check whether this expression is a return statement.
     * @param node This is the expression of part of java code to be checked.
     * @return boolean This returns whether the given constructor call is part of a return statement
     */
    private boolean notAReturnStatement(ASTConstructorCall node) {
        return !(node.getParent() instanceof ASTReturnStatement);
    }

    /**
     * This method is used to check whether this expression is not in a loop.
     * @param node This is the expression of part of java code to be checked.
     * @return boolean <code>false</code> if the given node is inside a loop, <code>true</code> otherwise
     */
    private boolean notInsideLoop(ASTConstructorCall node) {
        Node n = node;
        while (n != null) {
            if (n instanceof ASTLoopStatement) {
                return false;
            } else if (n instanceof ASTForInit) {
                /*
                 * init part is not technically inside the loop. Skip parent
                 * ASTForStatement but continue higher up to detect nested loops
                 */
                n = n.getParent();
            } else if (n.getParent() instanceof ASTForeachStatement && n.getParent().getNumChildren() > 1
                    && n == n.getParent().getChild(1)) {
                // it is the second child of a ForeachStatement.
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
