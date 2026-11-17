/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.reporting.RuleContext;

public class UnnecessaryStringConcatenationRule extends AbstractJavaRulechainRule {

    public UnnecessaryStringConcatenationRule() {
        super(ASTInfixExpression.class);
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

    private static boolean isRedundant(ASTInfixExpression concat, ASTExpression emptyString) {
        ASTExpression sibling = concat.getLeftOperand() == emptyString
                                ? concat.getRightOperand() : concat.getLeftOperand();
        if (JavaAstUtils.isStringConcatExpr(sibling)
            || TypeTestUtil.isExactlyA(String.class, sibling) && sibling.getConstValue() instanceof String) {
            // Both a concatenation and a constant string are already non-null strings.
            return true;
        }
        if (!JavaAstUtils.isStringConcatExpr(concat.getParent())) {
            return false;
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
}
