/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.properties.PropertyFactory.enumProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPostfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreDecrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreIncrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.performance.AbstractOptimizationRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class AvoidReassigningLoopVariablesRule extends AbstractOptimizationRule {

    private static final Map<String, ForeachReassignOption> FOREACH_REASSIGN_VALUES;

    static {
        final Map<String, ForeachReassignOption> map = new HashMap<>();
        map.put("deny", ForeachReassignOption.DENY);
        map.put("firstOnly", ForeachReassignOption.FIRST_ONLY);
        map.put("allow", ForeachReassignOption.ALLOW);
        FOREACH_REASSIGN_VALUES = Collections.unmodifiableMap(map);
    }

    private static final PropertyDescriptor<ForeachReassignOption> FOREACH_REASSIGN
            = enumProperty("foreachReassign", FOREACH_REASSIGN_VALUES)
            .defaultValue(ForeachReassignOption.DENY)
            .desc("how/if foreach control variables may be reassigned")
            .build();

    private static final Map<String, ForReassignOption> FOR_REASSIGN_VALUES;

    static {
        final Map<String, ForReassignOption> map = new HashMap<>();
        map.put("deny", ForReassignOption.DENY);
        map.put("skip", ForReassignOption.SKIP);
        map.put("allow", ForReassignOption.ALLOW);
        FOR_REASSIGN_VALUES = Collections.unmodifiableMap(map);
    }

    private static final PropertyDescriptor<ForReassignOption> FOR_REASSIGN
            = enumProperty("forReassign", FOR_REASSIGN_VALUES)
            .defaultValue(ForReassignOption.DENY)
            .desc("how/if for control variables may be reassigned")
            .build();

    public AvoidReassigningLoopVariablesRule() {
        definePropertyDescriptor(FOREACH_REASSIGN);
        definePropertyDescriptor(FOR_REASSIGN);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        final Set<String> loopVariables = new HashSet<>();
        for (ASTVariableDeclaratorId declaratorId : node.findDescendantsOfType(ASTVariableDeclaratorId.class)) {
            loopVariables.add(declaratorId.getImage());
        }

        if (node.jjtGetParent() instanceof ASTForInit) {
            // regular for loop: LocalVariableDeclaration -> ForInit -> ForStatement
            final ASTStatement loopBody = node.jjtGetParent().jjtGetParent().getFirstChildOfType(ASTStatement.class);
            final ForReassignOption forReassign = getProperty(FOR_REASSIGN);

            if (forReassign != ForReassignOption.ALLOW) {
                // check assignments
                checkAssignExceptIncrement(data, loopVariables, loopBody, false);

                if (forReassign != ForReassignOption.SKIP) {
                    // skipping not allowed -> also check increments
                    checkIncrementAndDecrement(data, loopVariables, loopBody, false);
                }
            }

        } else if (node.jjtGetParent() instanceof ASTForStatement) {
            // for-each loop: LocalVariableDeclaration -> ForStatement
            final ASTStatement loopBody = node.jjtGetParent().getFirstChildOfType(ASTStatement.class);
            final ForeachReassignOption foreachReassign = getProperty(FOREACH_REASSIGN);

            if (foreachReassign != ForeachReassignOption.ALLOW) {
                final boolean ignoreFirst = foreachReassign == ForeachReassignOption.FIRST_ONLY;
                checkAssignExceptIncrement(data, loopVariables, loopBody, ignoreFirst);
                checkIncrementAndDecrement(data, loopVariables, loopBody, ignoreFirst);
            }
        }

        return data;
    }

    /**
     * Report usages of assignments except '+=' and '-='.
     *
     * @param ignoreFirst if the first statement in the loop body should be ignored
     */
    private void checkAssignExceptIncrement(Object data, Set<String> loopVariables, ASTStatement loopBody, boolean ignoreFirst) {
        checkAssignments(data, loopVariables, loopBody, false, ignoreFirst);
    }

    /**
     * Report usages of increments ('++', '--', '+=', '-=').
     *
     * @param ignoreFirst if the first statement in the loop body should be ignored
     */
    private void checkIncrementAndDecrement(Object data, Set<String> loopVariables, ASTStatement loopBody, boolean ignoreFirst) {

        // foo ++ and foo --
        for (ASTPostfixExpression expression : loopBody.findDescendantsOfType(ASTPostfixExpression.class)) {
            if (ignoreFirst && isFirstStatementInBlock(expression, loopBody)) {
                // ignore the first statement
                continue;
            }

            checkVariable(data, loopVariables, expression.getFirstDescendantOfType(ASTName.class));
        }

        // ++ foo
        for (ASTPreIncrementExpression expression : loopBody.findDescendantsOfType(ASTPreIncrementExpression.class)) {
            if (ignoreFirst && isFirstStatementInBlock(expression, loopBody)) {
                // ignore the first statement
                continue;
            }

            checkVariable(data, loopVariables, expression.getFirstDescendantOfType(ASTName.class));
        }

        // -- foo
        for (ASTPreDecrementExpression expression : loopBody.findDescendantsOfType(ASTPreDecrementExpression.class)) {
            if (ignoreFirst && isFirstStatementInBlock(expression, loopBody)) {
                // ignore the first statement
                continue;
            }

            checkVariable(data, loopVariables, expression.getFirstDescendantOfType(ASTName.class));
        }

        // foo += x and foo -= x
        checkAssignments(data, loopVariables, loopBody, true, ignoreFirst);
    }

    /**
     * Report usages of assignments.
     *
     * @param checkIncrement true: check only '+=' and '-=',
     *                       false: check all other assignments
     * @param ignoreFirst    if the first statement in the loop body should be ignored
     */
    private void checkAssignments(Object data, Set<String> loopVariables, ASTStatement loopBody, boolean checkIncrement, boolean ignoreFirst) {
        for (ASTAssignmentOperator operator : loopBody.findDescendantsOfType(ASTAssignmentOperator.class)) {
            // check if the current operator is an assign-increment or assign-decrement operator
            final String operatorImage = operator.getImage();
            final boolean isIncrement = "+=".equals(operatorImage) || "-=".equals(operatorImage);

            if (isIncrement != checkIncrement) {
                // wrong type of operator
                continue;
            }

            if (ignoreFirst && isFirstStatementInBlock(operator, loopBody)) {
                // ignore the first statement
                continue;
            }

            final ASTPrimaryExpression primaryExpression = operator.jjtGetParent().getFirstChildOfType(ASTPrimaryExpression.class);
            final ASTName name = primaryExpression.getFirstDescendantOfType(ASTName.class);
            if (name != null) {
                checkVariable(data, loopVariables, name);
            }

        }
    }

    /**
     * Check if the given node is the first statement in the block.
     */
    private boolean isFirstStatementInBlock(Node node, ASTStatement loopBody) {
        // find the statement of the operation and the loop body block statement
        final ASTStatement statement = node.getFirstParentOfType(ASTStatement.class);
        final ASTBlockStatement block = loopBody.getFirstDescendantOfType(ASTBlockStatement.class);

        if (statement == null || block == null) {
            return false;
        }

        // is the first statement in the loop body?
        return block.equals(statement.jjtGetParent()) && statement.jjtGetChildIndex() == 0;
    }

    /**
     * Add a violation, if the node image is one of the loop variables.
     */
    private void checkVariable(Object data, Set<String> loopVariables, AbstractNode node) {
        if (node != null && loopVariables.contains(node.getImage())) {
            addViolation(data, node, node.getImage());
        }
    }

    private enum ForeachReassignOption {
        /**
         * Deny reassigning the 'foreach' control variable
         */
        DENY,

        /**
         * Allow reassigning the 'foreach' control variable if it is the first statement in the loop body.
         */
        FIRST_ONLY,

        /**
         * Allow reassigning the 'foreach' control variable.
         */
        ALLOW,
    }

    private enum ForReassignOption {
        /**
         * Deny reassigning a 'for' control variable.
         */
        DENY,

        /**
         * Allow skipping elements by incrementing/decrementing the 'for' control variable.
         */
        SKIP,

        /**
         * Allow reassigning the 'for' control variable.
         */
        ALLOW,
    }


}
