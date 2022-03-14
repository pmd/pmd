/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.ast.BinaryOp.CONDITIONAL_AND;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.CONDITIONAL_OR;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.EQ;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.GE;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.GT;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.LE;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.LT;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.NE;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.opsWithGreaterPrecedence;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.areComplements;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.isBooleanLiteral;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.isInfixExprWithOperator;

import java.util.EnumSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;

public class SimplifyBooleanReturnsRule extends AbstractJavaRulechainRule {

    private static final Set<BinaryOp> NEGATABLE_OPS = EnumSet.of(EQ, NE, GT, LT, GE, LE);

    public SimplifyBooleanReturnsRule() {
        super(ASTReturnStatement.class);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        ASTExpression expr = node.getExpr();
        if (expr == null
            || !expr.getTypeMirror().isPrimitive(PrimitiveTypeKind.BOOLEAN)
            || !isThenBranchOfSomeIf(node)) {
            return null;
        }
        checkIf(node.ancestors(ASTIfStatement.class).firstOrThrow(), asCtx(data), expr);
        return null;
    }

    // Only explore the then branch. If we explore the else, then we'll report twice.
    // In the case if .. else, both branches need to be symmetric anyway.
    private boolean isThenBranchOfSomeIf(ASTReturnStatement node) {
        if (node.getParent() instanceof ASTIfStatement) {
            return node.getIndexInParent() == 1;
        }
        return node.getParent() instanceof ASTBlock
            && ((ASTBlock) node.getParent()).size() == 1
            && node.getParent().getParent() instanceof ASTIfStatement
            && node.getParent().getIndexInParent() == 1;
    }

    private void checkIf(ASTIfStatement node, RuleContext data, ASTExpression thenExpr) {
        // that's the case: if..then..return; return;
        ASTExpression elseExpr = getElseExpr(node);
        if (elseExpr == null) {
            return;
        }

        if (isBooleanLiteral(thenExpr) || isBooleanLiteral(elseExpr)) {
            String fix = needsToBeReportedWhenOneBranchIsBoolean(node.getCondition(), thenExpr, elseExpr);
            if (fix != null) {
                data.addViolation(node, fix);
            }
        } else if (areComplements(thenExpr, elseExpr)) {
            // if (foo) return !a;
            // else return a;
            data.addViolation(node, "return {condition};");
        }
    }

    /**
     * Whether refactoring an if-then-else to use shortcut operators
     * would require adding parentheses to respect operator precedence.
     * <pre>{@code
     * if (cond) true   else expr      ->  cond || expr
     * if (cond) false  else expr      -> !cond && expr
     * if (cond) expr   else true      -> !cond || expr
     * if (cond) expr   else false     ->  cond && expr
     * }</pre>
     * Note that both the `expr` and the `condition` may require parentheses
     * (if the cond has to be negated).
     */
    private String needsToBeReportedWhenOneBranchIsBoolean(ASTExpression condition,
                                                           ASTExpression thenExpr,
                                                           ASTExpression elseExpr) {
        // at least one of these is true
        boolean thenFalse = isBooleanLiteral(thenExpr, false);
        boolean thenTrue = isBooleanLiteral(thenExpr, true);
        boolean elseTrue = isBooleanLiteral(elseExpr, true);
        boolean elseFalse = isBooleanLiteral(elseExpr, false);
        assert thenFalse || elseFalse || thenTrue || elseTrue
            : "expected boolean branch";

        if (isBooleanLiteral(thenExpr) && isBooleanLiteral(elseExpr)) {
            // both are boolean
            if (thenTrue && elseFalse) {
                return "return {condition};";
            } else if (thenFalse && elseTrue) {
                return "return !{condition};";
            } else if (thenTrue) { // both are true
                return "return true;";
            } else { // both are false
                return "return false;";
            }
        }

        boolean conditionNegated = thenFalse || elseTrue;
        if (conditionNegated && needsNewParensWhenNegating(condition)) {
            return null;
        }

        BinaryOp op = thenFalse || elseFalse ? CONDITIONAL_AND : CONDITIONAL_OR;
        // the branch that is not a literal, if both are literals, prefers elseExpr
        ASTExpression branch = thenFalse || thenTrue ? elseExpr : thenExpr;


        if (doesNotNeedNewParensUnderInfix(condition, op)
            && doesNotNeedNewParensUnderInfix(branch, op)) {
            if (thenTrue) {
                return "return {condition} || {elseBranch};";
            } else if (thenFalse) {
                return "return !{condition} || {elseBranch};";
            } else if (elseTrue) {
                return "return !{condition} && {thenBranch};";
            } else {
                return "return {condition} && {thenBranch};";
            }
        }
        return null;
    }

    private static boolean needsNewParensWhenNegating(ASTExpression e) {
        if (e instanceof ASTPrimaryExpression
            || e instanceof ASTCastExpression
            // parenthesized expressions are primary
            || e.isParenthesized()
            // == -> !=
            || isInfixExprWithOperator(e, NEGATABLE_OPS)
            // !! ->
            || JavaAstUtils.isBooleanNegation(e)) {
            return false;
        } else if (isInfixExprWithOperator(e, CONDITIONAL_OR)
            || isInfixExprWithOperator(e, CONDITIONAL_AND)) {
            // negating these ops can be replaced with complement op
            // and the negation pushed down branches using De Morgan's laws
            ASTInfixExpression infix = (ASTInfixExpression) e;
            return needsNewParensWhenNegating(infix.getLeftOperand())
                || needsNewParensWhenNegating(infix.getRightOperand());
        }
        return true;
    }

    private static boolean doesNotNeedNewParensUnderInfix(ASTExpression e, BinaryOp op) {
        // those nodes have greater precedence than infix
        return e instanceof ASTPrimaryExpression
            || e instanceof ASTCastExpression
            || e instanceof ASTUnaryExpression
            || e.isParenthesized()
            || isInfixExprWithOperator(e, opsWithGreaterPrecedence(op));
    }

    private @Nullable ASTExpression getReturnExpr(JavaNode node) {
        if (node instanceof ASTReturnStatement) {
            return ((ASTReturnStatement) node).getExpr();
        } else if (node instanceof ASTBlock && ((ASTBlock) node).size() == 1) {
            return getReturnExpr(((ASTBlock) node).get(0));
        }
        return null;
    }

    private @Nullable ASTExpression getElseExpr(ASTIfStatement node) {
        return node.hasElse() ? getReturnExpr(node.getElseBranch())
                              : getReturnExpr(node.getNextSibling()); // may be followed immediately by return
    }


}
