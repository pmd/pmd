/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * Rule that detects UUID.toString().isEmpty() and similar calls which always return false
 * @since 7.17.0
 */
public class NoEmptyUUIDRule extends AbstractJavaRulechainRule {

    public NoEmptyUUIDRule() {
        super(ASTMethodCall.class, ASTInfixExpression.class);
    }

    @Override
    public Object visit(ASTMethodCall methodCall, Object data) {
        String methodName = methodCall.getMethodName();

        // Check isEmpty and isBlank calls
        if ("isEmpty".equals(methodName) || "isBlank".equals(methodName)) {
            if (isUUIDToStringExpression(methodCall.getQualifier())) {
                asCtx(data).addViolation(methodCall);
            }
            return null;
        }

        // Check length() == 0 calls
        if ("length".equals(methodName) && isUUIDToStringExpression(methodCall.getQualifier())) {
            JavaNode parent = methodCall.getParent();
            if (parent instanceof ASTInfixExpression) {
                ASTInfixExpression infix = (ASTInfixExpression) parent;
                if (isEqualityOperator(infix) && (isZeroLiteral(infix.getLeftOperand()) || isZeroLiteral(infix.getRightOperand()))) {
                    asCtx(data).addViolation(infix);
                }
            }
        }

        // Check equals("") calls
        if ("equals".equals(methodName) && isUUIDToStringExpression(methodCall.getQualifier()) && !methodCall.getArguments().isEmpty() && isEmptyStringLiteral(methodCall.getArguments().get(0))) {
            asCtx(data).addViolation(methodCall);
        }

        return null;
    }

    @Override
    public Object visit(ASTInfixExpression infix, Object data) {
        // Check "".equals(uuid.toString()) pattern
        if (isEqualityOperator(infix)) {
            ASTExpression left = infix.getLeftOperand();
            ASTExpression right = infix.getRightOperand();

            // Check if this is a method call to equals
            if (left instanceof ASTMethodCall) {
                ASTMethodCall methodCall = (ASTMethodCall) left;
                if ("equals".equals(methodCall.getMethodName()) && isEmptyStringLiteral(methodCall.getQualifier()) && isUUIDToStringExpression(methodCall.getArguments().get(0))) {
                    asCtx(data).addViolation(infix);
                }
            }

            // Check the reverse pattern
            if (right instanceof ASTMethodCall) {
                ASTMethodCall methodCall = (ASTMethodCall) right;
                if ("equals".equals(methodCall.getMethodName()) && isEmptyStringLiteral(methodCall.getQualifier()) && isUUIDToStringExpression(methodCall.getArguments().get(0))) {
                    asCtx(data).addViolation(infix);
                }
            }
        }
        return null;
    }

    private boolean isEqualityOperator(ASTInfixExpression infix) {
        String operator = infix.getOperator().toString();
        return "==".equals(operator) || "!=".equals(operator);
    }

    private boolean isUUIDToStringExpression(ASTExpression expr) {
        if (expr == null) {
            return false;
        }

        // Handle direct pattern: uuid.toString()
        if (expr instanceof ASTMethodCall) {
            ASTMethodCall methodCall = (ASTMethodCall) expr;
            if ("toString".equals(methodCall.getMethodName())) {
                return isUUIDExpression(methodCall.getQualifier());
            }

            // Handle chained patterns: uuid.toString().trim(), uuid.toString().toLowerCase(), etc.
            // We need to check if the qualifier is a UUID toString call
            return isUUIDToStringExpression(methodCall.getQualifier());
        }

        return false;
    }

    private boolean isUUIDExpression(ASTExpression expr) {
        if (expr == null) {
            return false;
        }

        // Check if the expression is directly of UUID type
        if (TypeTestUtil.isA("java.util.UUID", expr)) {
            return true;
        }

        // Check if it's variable access that refers to a UUID
        if (expr instanceof ASTVariableAccess) {
            ASTVariableAccess varAccess = (ASTVariableAccess) expr;
            JVariableSymbol sym = varAccess.getReferencedSym();
            if (sym != null) {
                JTypeMirror type = sym.getTypeMirror(Substitution.EMPTY);
                return TypeTestUtil.isA("java.util.UUID", type);
            }
        }

        // Check method calls that return UUID
        if (expr instanceof ASTMethodCall) {
            ASTMethodCall methodCall = (ASTMethodCall) expr;
            return TypeTestUtil.isA("java.util.UUID", methodCall);
        }

        return false;
    }

    private boolean isEmptyStringLiteral(ASTExpression expr) {
        if (expr instanceof ASTLiteral) {
            ASTLiteral literal = (ASTLiteral) expr;
            Object value = literal.getConstValue();
            return value instanceof String && ((String) value).isEmpty();
        }
        return false;
    }

    private boolean isZeroLiteral(ASTExpression expr) {
        if (expr instanceof ASTLiteral) {
            ASTLiteral literal = (ASTLiteral) expr;
            Object value = literal.getConstValue();
            return value instanceof Integer && ((Integer) value) == 0;
        }
        return false;
    }
}
