/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.ast.BinaryOp.CONDITIONAL_AND;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.CONDITIONAL_OR;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.INSTANCEOF;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.NE;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.isInfixExprWithOperator;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.getOtherOperandIfInInfixExpr;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.isBooleanNegation;
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

            if (!isNullCheck(nullCheckExpr, instanceOfSubject)) {
                return null;
            }

            if (negated != isInfixExprWithOperator(nullCheckExpr, NE)) {
                addViolation(data, nullCheckExpr);
            }
        }
        return null;
    }
}
