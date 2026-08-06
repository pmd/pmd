/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.ast.BinaryOp.CONDITIONAL_AND;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.CONDITIONAL_OR;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.INSTANCEOF;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.NE;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.getOtherOperandIfInInfixExpr;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.isBooleanNegation;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.isInfixExprWithOperator;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.isNullCheck;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.StablePathMatcher;


public class SimplifyConditionalRule extends AbstractJavaRulechainRule {

    public SimplifyConditionalRule() {
        super(ASTInfixExpression.class);
    }


    @Override
    public Object visit(ASTInfixExpression node, Object data) {
        if (node.getOperator() == INSTANCEOF) {

            StablePathMatcher instanceOfSubject = StablePathMatcher.matching(node.getLeftOperand());
            if (instanceOfSubject == null) {
                return null;
            }

            BinaryOp chainOp;
            boolean negated;
            ASTExpression sibling;
            if (isInfixExprWithOperator(node.getParent(), CONDITIONAL_AND)) {
                // a != null && ... && a instanceof T
                chainOp = CONDITIONAL_AND;
                negated = false;
                sibling = getOtherOperandIfInInfixExpr(node);
            } else if (isBooleanNegation(node.getParent())
                && isInfixExprWithOperator(node.getParent().getParent(), CONDITIONAL_OR)) {
                // a == null || ... || !(a instanceof T)
                chainOp = CONDITIONAL_OR;
                negated = true;
                sibling = getOtherOperandIfInInfixExpr(node.getParent());
            } else {
                return null;
            }

            // The null check and the instanceof might be separated by other
            // conditions in the same short-circuit chain, so look through the
            // whole sibling chain instead of only the directly adjacent operand.
            ASTExpression nullCheckExpr = findNullCheckInChain(sibling, chainOp, instanceOfSubject);
            if (nullCheckExpr == null) {
                return null;
            }

            if (negated != isInfixExprWithOperator(nullCheckExpr, NE)) {
                asCtx(data).addViolation(nullCheckExpr);
            }
        }
        return null;
    }

    /**
     * Searches the given expression (and any same-operator short-circuit chain
     * nested within it) for a null check on the provided subject. This handles
     * cases where the null check is not directly adjacent to the instanceof,
     * e.g. {@code a != null && other && a instanceof T}.
     */
    private static ASTExpression findNullCheckInChain(ASTExpression expr, BinaryOp chainOp, StablePathMatcher subject) {
        if (expr == null) {
            return null;
        }
        if (isInfixExprWithOperator(expr, chainOp)) {
            ASTExpression found = findNullCheckInChain(((ASTInfixExpression) expr).getLeftOperand(), chainOp, subject);
            if (found != null) {
                return found;
            }
            return findNullCheckInChain(((ASTInfixExpression) expr).getRightOperand(), chainOp, subject);
        }
        return isNullCheck(expr, subject) ? expr : null;
    }
}
