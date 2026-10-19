/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;


public class AddEmptyStringRule extends AbstractJavaRulechainRule {

    public AddEmptyStringRule() {
        super(ASTStringLiteral.class);
    }

    @Override
    public Object visit(ASTStringLiteral node, Object data) {
        if (!node.isEmpty()) {
            return null;
        }
        JavaNode parent = node.getParent();
        checkExpr(data, parent, node);
        if (parent instanceof ASTVariableDeclarator) {
            ASTVariableId varId = ((ASTVariableDeclarator) parent).getVarId();
            if (varId.hasModifiers(JModifier.FINAL)) {
                for (ASTNamedReferenceExpr usage : varId.getLocalUsages()) {
                    checkExpr(data, usage.getParent(), usage);
                }
            }
        }
        return null;
    }

    private void checkExpr(Object data, JavaNode parent, JavaNode emptyStringNode) {
        if (JavaAstUtils.isInfixExprWithOperator(parent, BinaryOp.ADD)
            && parent.ancestors(ASTAnnotation.class).isEmpty()
            && !isInlinedConstantConversion((ASTInfixExpression) parent, emptyStringNode)) {
            asCtx(data).addViolationWithPosition(parent, parent.getAstInfo(),
                emptyStringNode.getReportLocation(), getMessage());
        }
    }

    /**
     * Returns true when the other operand is a non-string compile-time constant that uses a
     * constant variable. {@code javac} inlines {@code DURATION + ""} to a string literal,
     * while {@code String.valueOf(DURATION)} stays a method call and is not a constant
     * expression. Pure literals such as {@code "" + 123} are still reported, as is
     * concatenation of two strings.
     */
    private static boolean isInlinedConstantConversion(ASTInfixExpression infix, JavaNode emptyStringNode) {
        ASTExpression leftOperand = infix.getLeftOperand();
        ASTExpression rightOperand = infix.getRightOperand();
        final ASTExpression otherOperand;
        if (leftOperand == emptyStringNode) {
            otherOperand = rightOperand;
        } else if (rightOperand == emptyStringNode) {
            otherOperand = leftOperand;
        } else {
            return false;
        }

        Object constantValue = otherOperand.getConstValue();
        return constantValue != null
            && !(constantValue instanceof String)
            && otherOperand.descendantsOrSelf().any(it -> it instanceof ASTNamedReferenceExpr);
    }
}
