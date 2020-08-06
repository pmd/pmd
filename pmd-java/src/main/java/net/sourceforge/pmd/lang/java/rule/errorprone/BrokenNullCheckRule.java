/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.QualifiableExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.StablePathMatcher;

public class BrokenNullCheckRule extends AbstractJavaRulechainRule {

    public BrokenNullCheckRule() {
        super(ASTInfixExpression.class);
    }

    @Override
    public Object visit(ASTInfixExpression node, Object data) {
        if (isBrokenNullCheck(node)) {
            addViolation(data, node);
        }
        return data;
    }

    private static boolean isBrokenNullCheck(ASTInfixExpression enclosingConditional) {
        ASTExpression left = enclosingConditional.getLeftOperand();
        if (!(left instanceof ASTInfixExpression)) {
            return false;
        }

        BinaryOp op = ((ASTInfixExpression) left).getOperator();
        if (op != BinaryOp.EQ && op != BinaryOp.NE) {
            return false;
        } else if (op == BinaryOp.NE && enclosingConditional.getOperator() == BinaryOp.CONDITIONAL_AND
            || op == BinaryOp.EQ && enclosingConditional.getOperator() == BinaryOp.CONDITIONAL_OR) {
            return false; // not problematic
        }

        ASTNullLiteral nullLit = left.children(ASTNullLiteral.class).first();
        if (nullLit == null) {
            return false;
        }

        ASTExpression otherChild = (ASTExpression) left.getChild(1 - nullLit.getIndexInParent());
        StablePathMatcher exprMatcher = StablePathMatcher.matching(otherChild);
        if (exprMatcher == null) {
            // cannot be matched, because it's not stable
            return false;
        }

        // otherwise we fail if the var is used in a non-null context

        return enclosingConditional.getRightOperand()
                                   .descendantsOrSelf()
                                   .<QualifiableExpression>map(NodeStream.asInstanceOf(ASTMethodCall.class, ASTFieldAccess.class))
                                   .map(QualifiableExpression::getQualifier)
                                   .filter(exprMatcher::matches)
                                   .nonEmpty();
    }

}
