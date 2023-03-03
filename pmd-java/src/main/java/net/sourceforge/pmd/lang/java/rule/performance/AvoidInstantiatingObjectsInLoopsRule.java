/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.Collection;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAccess;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class AvoidInstantiatingObjectsInLoopsRule extends AbstractJavaRulechainRule {

    public AvoidInstantiatingObjectsInLoopsRule() {
        super(ASTConstructorCall.class, ASTArrayAllocation.class);
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        checkNode(node, data);
        return data;
    }

    @Override
    public Object visit(ASTArrayAllocation node, Object data) {
        checkNode(node, data);
        return data;
    }

    private void checkNode(JavaNode node, Object data) {
        if (notInsideLoop(node)) {
            return;
        }

        if (notAThrowStatement(node)
                && notAReturnStatement(node)
                && notBreakFollowing(node)
                && notArrayAssignment(node)
                && notCollectionAccess(node)) {
            addViolation(data, node);
        }
    }

    private boolean notArrayAssignment(JavaNode node) {
        JavaNode childOfAssignment = node.ancestorsOrSelf()
                .filter(n -> n.getParent() instanceof ASTAssignmentExpression).first();

        if (childOfAssignment != null && childOfAssignment.getIndexInParent() == 1) {
            Node assignee = childOfAssignment.getParent().getFirstChild();
            return !(assignee instanceof ASTArrayAccess);
        }
        return true;
    }

    private boolean notCollectionAccess(JavaNode node) {
        // checks whether the given ConstructorCall/ArrayAllocation is
        // part of a MethodCall on a Collection.
        return node.ancestors(ASTArgumentList.class)
            .filter(n -> n.getParent() instanceof ASTMethodCall)
            .filter(n -> TypeTestUtil.isA(Collection.class, ((ASTMethodCall) n.getParent()).getQualifier()))
            .isEmpty();
    }

    private boolean notBreakFollowing(JavaNode node) {
        JavaNode statement = node.ancestors().filter(n -> n.getParent() instanceof ASTBlock).first();
        return statement == null || !(statement.getNextSibling() instanceof ASTBreakStatement);
    }

    /**
     * This method is used to check whether this expression is a throw statement.
     * @param node This is the expression of part of java code to be checked.
     * @return boolean This returns whether the given constructor call is part of a throw statement
     */
    private boolean notAThrowStatement(JavaNode node) {
        return !(node.getParent() instanceof ASTThrowStatement);
    }

    /**
     * This method is used to check whether this expression is a return statement.
     * @param node This is the expression of part of java code to be checked.
     * @return boolean This returns whether the given constructor call is part of a return statement
     */
    private boolean notAReturnStatement(JavaNode node) {
        return !(node.getParent() instanceof ASTReturnStatement);
    }

    /**
     * This method is used to check whether this expression is not in a loop.
     * @param node This is the expression of part of java code to be checked.
     * @return boolean <code>false</code> if the given node is inside a loop, <code>true</code> otherwise
     */
    private boolean notInsideLoop(Node node) {
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
