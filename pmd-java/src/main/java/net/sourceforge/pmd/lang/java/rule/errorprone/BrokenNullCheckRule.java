/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.QualifiableExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.rule.internal.StablePathMatcher;

public class BrokenNullCheckRule extends AbstractJavaRulechainRule {

    public BrokenNullCheckRule() {
        super(ASTInfixExpression.class);
    }

    @Override
    public Object visit(ASTInfixExpression node, Object data) {
        checkBrokenNullCheck(node, (RuleContext) data);
        return data;
    }

    private void checkBrokenNullCheck(ASTInfixExpression enclosingConditional, RuleContext ctx) {
        ASTExpression left = enclosingConditional.getLeftOperand();
        if (!(left instanceof ASTInfixExpression)) {
            return;
        }

        BinaryOp op = ((ASTInfixExpression) left).getOperator();
        if (op != BinaryOp.EQ && op != BinaryOp.NE) {
            return;
        } else if (op == BinaryOp.NE && enclosingConditional.getOperator() == BinaryOp.CONDITIONAL_AND
            || op == BinaryOp.EQ && enclosingConditional.getOperator() == BinaryOp.CONDITIONAL_OR) {
            return; // not problematic
        }

        ASTNullLiteral nullLit = left.children(ASTNullLiteral.class).first();
        if (nullLit == null) {
            return;
        }

        ASTExpression otherChild = JavaRuleUtil.getOtherOperandIfInInfixExpr(nullLit);
        StablePathMatcher pathToNullVar = StablePathMatcher.matching(otherChild);
        if (pathToNullVar == null) {
            // cannot be matched, because it's not stable
            return;
        }

        NodeStream<ASTExpression> exprsToCheck = enclosingConditional.getRightOperand()
                                                                     .descendantsOrSelf()
                                                                     .filterIs(ASTExpression.class);

        for (ASTExpression subexpr : exprsToCheck) {
            NpeReason npeReason = willNpeWithReason(subexpr, pathToNullVar);
            if (npeReason != null) {
                addViolationWithMessage(ctx, subexpr, npeReason.formatMessage);
            }
        }

    }

    private static NpeReason willNpeWithReason(ASTExpression e, StablePathMatcher pathToNullVar) {
        if (e instanceof QualifiableExpression) {
            ASTExpression qualifier = ((QualifiableExpression) e).getQualifier();
            if (pathToNullVar.matches(qualifier)) {
                return NpeReason.DEREFERENCE;
            }
        }

        if (e.getParent() instanceof ASTInfixExpression) {
            ASTInfixExpression infix = (ASTInfixExpression) e.getParent();
            if (pathToNullVar.matches(e) && operatorUnboxesOperand(infix)) {
                return NpeReason.UNBOXING;
            }
        }
        return null;
    }

    private static boolean operatorUnboxesOperand(ASTInfixExpression infix) {
        BinaryOp operator = infix.getOperator();

        if (operator == BinaryOp.INSTANCEOF) {
            return false;
        }

        boolean leftIsPrimitive = infix.getLeftOperand().getTypeMirror().isPrimitive();
        boolean rightIsPrimitive = infix.getRightOperand().getTypeMirror().isPrimitive();
        if (leftIsPrimitive != rightIsPrimitive) {
            return true;
        } else {
            assert !leftIsPrimitive || !rightIsPrimitive : "We know at least one of the operands is null";
            // So both are reference types
            // With these ops, in this case no unboxing takes place
            return operator != BinaryOp.NE && operator != BinaryOp.EQ;
        }
    }

    enum NpeReason {
        DEREFERENCE("Dereferencing the qualifier of this expression will throw a NullPointerException"),
        UNBOXING("Unboxing this operand will throw a NullPointerException");

        private final String formatMessage;

        NpeReason(String formatMessage) {
            this.formatMessage = formatMessage;
        }
    }

}
