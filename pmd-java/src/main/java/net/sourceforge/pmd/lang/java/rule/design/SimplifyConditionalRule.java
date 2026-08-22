/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.ast.BinaryOp.CONDITIONAL_AND;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.CONDITIONAL_OR;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.INSTANCEOF;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.NE;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.isBooleanNegation;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.isInfixExprWithOperator;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.isNullCheck;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
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
            // the operand of the chain that holds the instanceof, skipped when scanning the chain
            ASTExpression instanceofOperand = node;
            if (isInfixExprWithOperator(node.getParent(), CONDITIONAL_AND)) {
                // a != null && ... && a instanceof T
                chainOp = CONDITIONAL_AND;
                negated = false;
            } else if (isBooleanNegation(node.getParent())
                && isInfixExprWithOperator(node.getParent().getParent(), CONDITIONAL_OR)) {
                // a == null || ... || !(a instanceof T)
                chainOp = CONDITIONAL_OR;
                negated = true;
                instanceofOperand = (ASTExpression) node.getParent();
            } else {
                return null;
            }

            // The null check and the instanceof might sit anywhere in the same
            // short-circuit chain, separated by other conditions, so scan every
            // operand of the outermost chain instead of only the direct sibling.
            ASTInfixExpression chain = (ASTInfixExpression) instanceofOperand.getParent();
            while (isInfixExprWithOperator(chain.getParent(), chainOp)) {
                chain = (ASTInfixExpression) chain.getParent();
            }

            List<ASTExpression> operands = new ArrayList<>();
            collectLeaves(chain, chainOp, operands);

            // with several instanceofs on the subject, only the first one reports
            for (ASTExpression leaf : operands) {
                if (leaf == instanceofOperand) {
                    break;
                }
                if (isSameSubjectInstanceof(leaf, chainOp, instanceOfSubject)) {
                    return null;
                }
            }
            for (ASTExpression leaf : operands) {
                if (leaf != instanceofOperand && isNullCheck(leaf, instanceOfSubject)
                    && negated != isInfixExprWithOperator(leaf, NE)) {
                    asCtx(data).addViolation(leaf);
                }
            }
        }
        return null;
    }

    /**
     * Collects the leaf operands of the short-circuit chain in evaluation order.
     */
    private static void collectLeaves(ASTExpression expr, BinaryOp chainOp, List<ASTExpression> leaves) {
        if (isInfixExprWithOperator(expr, chainOp)) {
            ASTInfixExpression infix = (ASTInfixExpression) expr;
            collectLeaves(infix.getLeftOperand(), chainOp, leaves);
            collectLeaves(infix.getRightOperand(), chainOp, leaves);
        } else {
            leaves.add(expr);
        }
    }

    /**
     * Returns true if the operand is an instanceof on the subject, in the form
     * that reports the chain (bare in a && chain, negated in a || chain).
     */
    private static boolean isSameSubjectInstanceof(ASTExpression expr, BinaryOp chainOp, StablePathMatcher subject) {
        if (chainOp == CONDITIONAL_OR) {
            if (!isBooleanNegation(expr)) {
                return false;
            }
            expr = ((ASTUnaryExpression) expr).getOperand();
        }
        return isInfixExprWithOperator(expr, INSTANCEOF)
            && subject.matches(((ASTInfixExpression) expr).getLeftOperand());
    }
}
