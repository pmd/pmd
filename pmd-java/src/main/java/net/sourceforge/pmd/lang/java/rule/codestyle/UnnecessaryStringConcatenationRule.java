/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.AssignmentOp;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.AssignmentEntry;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.ReachingDefinitionSet;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.reporting.RuleContext;

public class UnnecessaryStringConcatenationRule extends AbstractJavaRulechainRule {

    public UnnecessaryStringConcatenationRule() {
        super(ASTInfixExpression.class, ASTAssignmentExpression.class);
    }

    @Override
    public Object visit(ASTInfixExpression node, Object data) {
        if (!JavaAstUtils.isStringConcatExpr(node)
            || JavaAstUtils.isStringConcatExpr(node.getParent())) {
            return null;
        }

        for (ASTExpression operand : JavaAstUtils.flattenOperands(node)) {
            if (operand instanceof ASTStringLiteral && ((ASTStringLiteral) operand).isEmpty()
                && isRedundant((ASTInfixExpression) operand.getParent(), operand)) {
                ((RuleContext) data).addViolation(operand);
                // Report only one removal at a time: in "" + s + "", one empty
                // string must remain to convert a possibly null s to "null".
                break;
            }
        }
        return null;
    }

    @Override
    public Object visit(ASTAssignmentExpression node, Object data) {
        ASTExpression rhs = node.getRightOperand();
        if (isStringAppend(node)
            && rhs instanceof ASTStringLiteral && ((ASTStringLiteral) rhs).isEmpty()
            && node.getLeftOperand() instanceof ASTVariableAccess
            && ((ASTVariableAccess) node.getLeftOperand()).getReferencedSym() instanceof JLocalVariableSymbol
            && isKnownNonNullString(node.getLeftOperand())) {
            ((RuleContext) data).addViolation(rhs);
        }
        return null;
    }

    private static boolean isRedundant(ASTInfixExpression concat, ASTExpression emptyString) {
        ASTExpression sibling = concat.getLeftOperand() == emptyString
                                ? concat.getRightOperand() : concat.getLeftOperand();
        if (isNonNullStringValue(sibling)) {
            return true;
        }
        if (!JavaAstUtils.isStringConcatExpr(concat.getParent())) {
            return isKnownNonNullString(sibling);
        }

        // Inspect the original parent rather than reassociating operands. For
        // example, "" + 1 + 2 + "a" needs its empty string to produce "12a".
        if (TypeTestUtil.isExactlyA(String.class, sibling)) {
            // The enclosing concatenation still converts null to "null".
            return true;
        }
        JTypeMirror siblingType = sibling.getTypeMirror();
        ASTInfixExpression parent = (ASTInfixExpression) concat.getParent();
        // On the right, the enclosing left operand has already been evaluated.
        // Moving conversion to that concatenation preserves its evaluation order.
        return TypeTestUtil.isExactlyA(String.class, JavaAstUtils.getOtherOperandIfInInfixExpr(concat))
            && (siblingType.isPrimitive() || siblingType.isBottom()
                || parent.getRightOperand() == concat && !TypeOps.isUnresolvedOrArray(siblingType));
    }

    private static boolean isNonNullStringValue(ASTExpression expression) {
        return JavaAstUtils.isStringConcatExpr(expression)
            || TypeTestUtil.isExactlyA(String.class, expression) && expression.getConstValue() instanceof String;
    }

    private static boolean isStringAppend(ASTAssignmentExpression assignment) {
        return assignment.getOperator() == AssignmentOp.ADD_ASSIGN
            && TypeTestUtil.isExactlyA(String.class, assignment.getLeftOperand());
    }

    private static boolean isKnownNonNullString(ASTExpression expression) {
        if (isNonNullStringValue(expression)) {
            return true;
        }
        if (!(expression instanceof ASTVariableAccess) || !TypeTestUtil.isExactlyA(String.class, expression)) {
            return false;
        }
        ASTVariableAccess variable = (ASTVariableAccess) expression;
        if (!(variable.getReferencedSym() instanceof JLocalVariableSymbol)) {
            return false;
        }
        ReachingDefinitionSet reaching = DataflowPass.getDataflowResult(expression.getRoot())
            .getReachingDefinitions(variable);
        if (reaching.isNotFullyKnown() || reaching.getReaching().isEmpty()) {
            return false;
        }
        for (AssignmentEntry definition : reaching.getReaching()) {
            ASTExpression rhs = definition.getRhsAsExpression();
            if (rhs == null) {
                return false;
            }
            // A string += produces a non-null result, even if its right operand is nullable.
            if (rhs.getParent() instanceof ASTAssignmentExpression
                && isStringAppend((ASTAssignmentExpression) rhs.getParent())) {
                continue;
            }
            if (!isNonNullStringValue(rhs)) {
                return false;
            }
        }
        return true;
    }
}
