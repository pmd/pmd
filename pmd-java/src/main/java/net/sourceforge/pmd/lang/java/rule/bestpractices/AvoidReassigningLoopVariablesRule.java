/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.properties.PropertyFactory.enumProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPostfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreDecrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreIncrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
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
        addRuleChainVisit(ASTLocalVariableDeclaration.class);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        final Set<String> loopVariables = new HashSet<>();
        for (ASTVariableDeclaratorId declaratorId : node.findDescendantsOfType(ASTVariableDeclaratorId.class)) {
            loopVariables.add(declaratorId.getImage());
        }

        if (node.getParent() instanceof ASTForInit) {
            // regular for loop: LocalVariableDeclaration -> ForInit -> ForStatement
            final ASTStatement loopBody = node.getParent().getParent().getFirstChildOfType(ASTStatement.class);
            final ForReassignOption forReassign = getProperty(FOR_REASSIGN);

            if (forReassign != ForReassignOption.ALLOW) {
                // check assignments
                checkAssignExceptIncrement(data, loopVariables, loopBody);

                if (forReassign == ForReassignOption.SKIP) {
                    // skipping allowed -> only check non-conditional increments
                    checkIncrementAndDecrement(data, loopVariables, loopBody, IgnoreFlags.IGNORE_CONDITIONAL);
                } else {
                    // skipping not allowed -> check all increments
                    checkIncrementAndDecrement(data, loopVariables, loopBody);
                }
            }

        } else if (node.getParent() instanceof ASTForStatement) {
            // for-each loop: LocalVariableDeclaration -> ForStatement
            final ASTStatement loopBody = node.getParent().getFirstChildOfType(ASTStatement.class);
            final ForeachReassignOption foreachReassign = getProperty(FOREACH_REASSIGN);

            if (foreachReassign == ForeachReassignOption.FIRST_ONLY) {
                checkAssignExceptIncrement(data, loopVariables, loopBody, IgnoreFlags.IGNORE_FIRST);
                checkIncrementAndDecrement(data, loopVariables, loopBody, IgnoreFlags.IGNORE_FIRST);

            } else if (foreachReassign == ForeachReassignOption.DENY) {
                checkAssignExceptIncrement(data, loopVariables, loopBody);
                checkIncrementAndDecrement(data, loopVariables, loopBody);
            }
        }

        return data;
    }

    /**
     * Report usages of assignments except '+=' and '-='.
     *
     * @param ignoreFlags which statements should be ignored
     */
    private void checkAssignExceptIncrement(Object data, Set<String> loopVariables, ASTStatement loopBody, IgnoreFlags... ignoreFlags) {
        checkAssignments(data, loopVariables, loopBody, false, ignoreFlags);
    }

    /**
     * Report usages of increments ('++', '--', '+=', '-=').
     *
     * @param ignoreFlags which statements should be ignored
     */
    private void checkIncrementAndDecrement(Object data, Set<String> loopVariables, ASTStatement loopBody, IgnoreFlags... ignoreFlags) {

        // foo ++ and foo --
        for (ASTPostfixExpression expression : loopBody.findDescendantsOfType(ASTPostfixExpression.class)) {
            if (ignoreNode(expression, loopBody, ignoreFlags)) {
                continue;
            }

            checkVariable(data, loopVariables, singleVariableName(expression.getFirstDescendantOfType(ASTPrimaryExpression.class)));
        }

        // ++ foo
        for (ASTPreIncrementExpression expression : loopBody.findDescendantsOfType(ASTPreIncrementExpression.class)) {
            if (ignoreNode(expression, loopBody, ignoreFlags)) {
                continue;
            }

            checkVariable(data, loopVariables, singleVariableName(expression.getFirstDescendantOfType(ASTPrimaryExpression.class)));
        }

        // -- foo
        for (ASTPreDecrementExpression expression : loopBody.findDescendantsOfType(ASTPreDecrementExpression.class)) {
            if (ignoreNode(expression, loopBody, ignoreFlags)) {
                continue;
            }

            checkVariable(data, loopVariables, singleVariableName(expression.getFirstDescendantOfType(ASTPrimaryExpression.class)));
        }

        // foo += x and foo -= x
        checkAssignments(data, loopVariables, loopBody, true, ignoreFlags);
    }

    /**
     * Report usages of assignments.
     *
     * @param checkIncrement true: check only '+=' and '-=',
     *                       false: check all other assignments
     * @param ignoreFlags    which statements should be ignored
     */
    private void checkAssignments(Object data, Set<String> loopVariables, ASTStatement loopBody, boolean checkIncrement, IgnoreFlags... ignoreFlags) {
        for (ASTAssignmentOperator operator : loopBody.findDescendantsOfType(ASTAssignmentOperator.class)) {
            // check if the current operator is an assign-increment or assign-decrement operator
            final String operatorImage = operator.getImage();
            final boolean isIncrement = "+=".equals(operatorImage) || "-=".equals(operatorImage);

            if (isIncrement != checkIncrement) {
                // wrong type of operator
                continue;
            }

            if (ignoreNode(operator, loopBody, ignoreFlags)) {
                continue;
            }

            final ASTPrimaryExpression primaryExpression = operator.getParent().getFirstChildOfType(ASTPrimaryExpression.class);
            checkVariable(data, loopVariables, singleVariableName(primaryExpression));
        }
    }

    /**
     * Check if the node should be ignored, depending on the given flags and the context.
     */
    private boolean ignoreNode(Node node, ASTStatement loopBody, IgnoreFlags... ignoreFlags) {
        if (ignoreFlags.length == 0) {
            return false;
        }
        final List<IgnoreFlags> ignoreFlagsList = Arrays.asList(ignoreFlags);

        // ignore the first statement
        final boolean ignoredFirstStatement = ignoreFlagsList.contains(IgnoreFlags.IGNORE_FIRST) && isFirstStatementInBlock(node, loopBody);

        // ignore conditionally executed statement
        final boolean ignoredConditional = ignoreFlagsList.contains(IgnoreFlags.IGNORE_CONDITIONAL) && isConditionallyExecuted(node, loopBody);

        return ignoredFirstStatement || ignoredConditional;
    }

    /**
     * Extracts the variable name by traversing PrimaryExpression -> PrimaryPrefix -> Name.
     * Also check if there is a PrimaryExpression -> PrimarySuffix which indicates a field or array access.
     *
     * @returns the Name or null if the PrimaryPrefix is "this" or "super"
     */
    private ASTName singleVariableName(ASTPrimaryExpression primaryExpression) {
        final ASTPrimaryPrefix primaryPrefix = primaryExpression.getFirstChildOfType(ASTPrimaryPrefix.class);
        final ASTPrimarySuffix primarySuffix = primaryExpression.getFirstChildOfType(ASTPrimarySuffix.class);

        if (primarySuffix != null || primaryPrefix == null) {
            return null;
        }

        return primaryPrefix.getFirstChildOfType(ASTName.class);
    }

    /**
     * Check if the given node is the first statement in the block.
     */
    private boolean isFirstStatementInBlock(Node node, ASTStatement loopBody) {
        // find the statement of the operation and the loop body block statement
        final ASTBlockStatement statement = node.getFirstParentOfType(ASTBlockStatement.class);
        final ASTBlock block = loopBody.getFirstDescendantOfType(ASTBlock.class);

        if (statement == null || block == null) {
            return false;
        }

        // is the first statement in the loop body?
        return block.equals(statement.getParent()) && statement.getIndexInParent() == 0;
    }

    /**
     * Check if the node will only be executed conditionally by checking,
     * if the node is inside any kind of control flow statement or
     * if any prior statement contains a {@code continue} statement.
     * <br>
     * This doesn't check
     */
    private boolean isConditionallyExecuted(Node node, ASTStatement loopBody) {
        // starting at the assignment/increment node, traverse the tree up to
        // check if we're inside the conditionally executed block of a control flow statement

        Node checkNode = node;
        while (checkNode.getParent() != null && !checkNode.getParent().equals(loopBody)) {
            final Node parent = checkNode.getParent();

            // if/switch/while-statement, excluding the expression
            if (parent instanceof ASTIfStatement || parent instanceof ASTSwitchStatement || parent instanceof ASTWhileStatement || parent instanceof ASTDoStatement) {
                return !(checkNode instanceof ASTExpression);
            }

            // for-statement, excluding the initializer, expression and update
            if (parent instanceof ASTForStatement) {
                return !(checkNode instanceof ASTForInit || checkNode instanceof ASTExpression || checkNode instanceof ASTForUpdate);
            }
            checkNode = parent;
        }

        // iterating the statements of the loop body, check if there is a
        // continue statement before the increment statement
        final ASTBlock block = loopBody.getFirstDescendantOfType(ASTBlock.class);
        if (block != null) {
            for (int i = 0; i < block.getNumChildren(); i++) {
                final Node statement = block.getChild(i);

                if (statement.hasDescendantOfType(ASTContinueStatement.class)) {
                    return true;
                }
                if (isParent(statement, node)) {
                    return false;
                }
            }
        }

        return false;
    }

    private boolean isParent(Node possibleParent, Node node) {
        Node checkNode = node;
        while (checkNode.getParent() != null) {
            if (checkNode.getParent().equals(possibleParent)) {
                return true;
            }
            checkNode = checkNode.getParent();
        }
        return false;
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
        ALLOW;

        /**
         * The RuleDocGenerator uses toString() to determine the default value.
         *
         * @return the mapped property value instead of the enum name
         */
        @Override
        public String toString() {
            for (Map.Entry<String, ForeachReassignOption> entry : FOREACH_REASSIGN_VALUES.entrySet()) {
                if (entry.getValue().equals(this)) {
                    return entry.getKey();
                }
            }

            // fallback
            return super.toString();
        }
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
        ALLOW;

        /**
         * The RuleDocGenerator uses toString() to determine the default value.
         *
         * @return the mapped property value instead of the enum name
         */
        @Override
        public String toString() {
            for (Map.Entry<String, ForReassignOption> entry : FOR_REASSIGN_VALUES.entrySet()) {
                if (entry.getValue().equals(this)) {
                    return entry.getKey();
                }
            }

            // fallback
            return super.toString();
        }
    }

    private enum IgnoreFlags {

        IGNORE_FIRST,

        IGNORE_CONDITIONAL

    }
}
