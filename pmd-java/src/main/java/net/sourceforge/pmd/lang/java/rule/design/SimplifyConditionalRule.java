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

            ASTExpression nullCheckExpr;
            boolean negated;
            if (isInfixExprWithOperator(node.getParent(), CONDITIONAL_AND)) {
                // a != null && a instanceof T
                negated = false;
                nullCheckExpr = getOtherOperandIfInInfixExpr(node);
            } else if (isBooleanNegation(node.getParent())
                && isInfixExprWithOperator(node.getParent().getParent(), CONDITIONAL_OR)) {
                // a == null || a instanceof T
                negated = true;
                nullCheckExpr = getOtherOperandIfInInfixExpr(node.getParent());
            } else {
                return null;
            }

            // Check if nullCheckExpr is a null check on the instanceOfSubject
            // If not, it might be a && or || chain, so we need to check all operands
            if (!isNullCheck(nullCheckExpr, instanceOfSubject)) {
                // Check if nullCheckExpr is a && expression and contains a null check
                if (isInfixExprWithOperator(nullCheckExpr, CONDITIONAL_AND)) {
                    if (!containsNullCheckInChain((ASTInfixExpression) nullCheckExpr, instanceOfSubject, CONDITIONAL_AND)) {
                        return null;
                    }
                } else if (isInfixExprWithOperator(nullCheckExpr, CONDITIONAL_OR)) {
                    // For || case: a == null || b == null || !(a instanceof T)
                    if (!containsNullCheckInChain((ASTInfixExpression) nullCheckExpr, instanceOfSubject, CONDITIONAL_OR)) {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            if (negated != isInfixExprWithOperator(nullCheckExpr, NE)) {
                asCtx(data).addViolation(nullCheckExpr);
            }
        }
        return null;
    }
    
    /**
     * Check if a && or || expression chain contains a null check on the given subject.
     */
    private boolean containsNullCheckInChain(ASTInfixExpression expr, StablePathMatcher subject, net.sourceforge.pmd.lang.java.ast.BinaryOp operator) {
        // Check left operand
        if (isNullCheck(expr.getLeftOperand(), subject)) {
            return true;
        }
        // If left operand is also a same operator expression, recurse
        if (isInfixExprWithOperator(expr.getLeftOperand(), operator)) {
            if (containsNullCheckInChain((ASTInfixExpression) expr.getLeftOperand(), subject, operator)) {
                return true;
            }
        }
        // Check right operand
        if (isNullCheck(expr.getRightOperand(), subject)) {
            return true;
        }
        // If right operand is also a same operator expression, recurse
        if (isInfixExprWithOperator(expr.getRightOperand(), operator)) {
            if (containsNullCheckInChain((ASTInfixExpression) expr.getRightOperand(), subject, operator)) {
                return true;
            }
        }
        return false;
    }
}