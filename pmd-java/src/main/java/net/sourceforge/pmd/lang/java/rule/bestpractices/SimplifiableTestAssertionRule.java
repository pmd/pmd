/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.isBooleanLiteral;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 *
 */
public class SimplifiableTestAssertionRule extends AbstractJavaRulechainRule {

    private static final InvocationMatcher OBJECT_EQUALS = InvocationMatcher.parse("_#equals(java.lang.Object)");

    public SimplifiableTestAssertionRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        RuleContext ctx = (RuleContext) data;

        final boolean isAssertTrue = isAssertionCall(node, "assertTrue");
        final boolean isAssertFalse = isAssertionCall(node, "assertFalse");

        if (isAssertTrue || isAssertFalse) {
            ASTExpression condition = getCondition(node);
            ASTInfixExpression eq = asEqualityExpr(condition);
            if (eq != null) {
                boolean isPositive = isPositiveEqualityExpr(eq) == isAssertTrue;
                final String suggestion;
                if (JavaAstUtils.isNullLiteral(eq.getLeftOperand())
                    || JavaAstUtils.isNullLiteral(eq.getRightOperand())) {
                    // use assertNull/assertNotNull
                    suggestion = isPositive ? "assertNull" : "assertNotNull";
                } else {
                    if (isPrimitive(eq.getLeftOperand()) || isPrimitive(eq.getRightOperand())) {
                        suggestion = isPositive ? "assertEquals" : "assertNotEquals";
                    } else {
                        suggestion = isPositive ? "assertSame" : "assertNotSame";
                    }
                }
                ctx.addViolation(node, suggestion);

            } else {
                @Nullable ASTExpression negatedExprOperand = getNegatedExprOperand(condition);

                if (OBJECT_EQUALS.matchesCall(negatedExprOperand)) {
                    //assertTrue(!a.equals(b))
                    String suggestion = isAssertTrue ? "assertNotEquals" : "assertEquals";
                    ctx.addViolation(node, suggestion);

                } else if (negatedExprOperand != null) {
                    //assertTrue(!something)
                    String suggestion = isAssertTrue ? "assertFalse" : "assertTrue";
                    ctx.addViolation(node, suggestion);

                } else if (OBJECT_EQUALS.matchesCall(condition)) {
                    //assertTrue(a.equals(b))
                    String suggestion = isAssertTrue ? "assertEquals" : "assertNotEquals";
                    ctx.addViolation(node, suggestion);

                } else if (isAssertTrue && isInstanceOfType(condition) && hasAssertInstanceOf(node)) {
                    //assertTrue(a instanceof A)
                    ctx.addViolation(node, "assertInstanceOf");
                }
            }
        }

        boolean isAssertEquals = isAssertionCall(node, "assertEquals");
        boolean isAssertNotEquals = isAssertionCall(node, "assertNotEquals");

        if (isAssertEquals || isAssertNotEquals) {
            ASTArgumentList argList = node.getArguments();
            if (argList.size() >= 2) {
                int first = getFirstComparedArgIndex(node);
                ASTExpression comp0 = argList.get(first);
                ASTExpression comp1 = argList.get(first + 1);
                if (isAssertEquals && isDifferentConstants(node, comp0, comp1)) {
                    //assertEquals(1, 2) always fails
                    ctx.addViolation(node, "fail");

                } else if (isBooleanLiteral(comp0) ^ isBooleanLiteral(comp1)) {
                    if (isBooleanLiteral(comp1)) {
                        ASTExpression tmp = comp0;
                        comp0 = comp1;
                        comp1 = tmp;
                    }
                    // now the literal is in comp0 and the other is some expr
                    if (comp1.getTypeMirror().isPrimitive(PrimitiveTypeKind.BOOLEAN)) {
                        ASTBooleanLiteral literal = (ASTBooleanLiteral) comp0;
                        String suggestion = literal.isTrue() == isAssertEquals ? "assertTrue" : "assertFalse";
                        ctx.addViolation(node, suggestion);
                    }

                } else if (JavaAstUtils.isNullLiteral(comp0) || JavaAstUtils.isNullLiteral(comp1)) {
                    //assertEquals(null, a)
                    String suggestion = isAssertEquals ? "assertNull" : "assertNotNull";
                    ctx.addViolation(node, suggestion);
                }
            }
        }

        return null;
    }

    private boolean isPrimitive(ASTExpression node) {
        return node.getTypeMirror().isPrimitive();
    }

    /**
     * Returns the {@code boolean} argument of an assertTrue or assertFalse call,
     * or null for the overloads that take a {@code BooleanSupplier}. JUnit 3 and 4
     * take the message first, while JUnit 5 and TestNG take it last.
     */
    private static @Nullable ASTExpression getCondition(ASTMethodCall call) {
        List<JTypeMirror> formals = call.getMethodType().getFormalParameters();
        for (int i = 0; i < formals.size(); i++) {
            if (formals.get(i).isPrimitive(PrimitiveTypeKind.BOOLEAN)) {
                return call.getArguments().get(i);
            }
        }
        return null;
    }

    /**
     * Returns the index of the first of the two values compared by an assertEquals
     * or assertNotEquals call. JUnit 3 and 4 take an optional message first, while
     * JUnit 5 and TestNG take it last.
     */
    private static int getFirstComparedArgIndex(ASTMethodCall call) {
        JMethodSig method = call.getMethodType();
        List<JTypeMirror> formals = method.getFormalParameters();
        boolean isJUnit3Or4 = TypeTestUtil.isA("org.junit.Assert", method.getDeclaringType())
            || TypeTestUtil.isA("junit.framework.Assert", method.getDeclaringType());
        return isJUnit3Or4 && formals.size() > 2 && TypeTestUtil.isExactlyA(String.class, formals.get(0)) ? 1 : 0;
    }

    /**
     * True if both values are compile-time constants of the same type with different
     * values, so that assertEquals always fails. Floating point overloads are left out,
     * as they may compare with a delta.
     */
    private static boolean isDifferentConstants(ASTMethodCall call, ASTExpression a, ASTExpression b) {
        Object valueA = a.getConstValue();
        Object valueB = b.getConstValue();
        return valueA != null && valueB != null && !valueA.equals(valueB)
            && a.getTypeMirror().equals(b.getTypeMirror())
            && call.getMethodType().getFormalParameters().stream().noneMatch(JTypeMirror::isFloatingPoint);
    }

    private static boolean isInstanceOfType(@Nullable ASTExpression node) {
        return JavaAstUtils.isInfixExprWithOperator(node, BinaryOp.INSTANCEOF)
            && ((ASTInfixExpression) node).getRightOperand() instanceof ASTTypeExpression;
    }

    /**
     * True if the assertion class declares assertInstanceOf, as JUnit 5.8 and later do.
     */
    private static boolean hasAssertInstanceOf(ASTMethodCall call) {
        JTypeDeclSymbol assertions = call.getMethodType().getDeclaringType().getSymbol();
        return assertions instanceof JClassSymbol
            && ((JClassSymbol) assertions).getDeclaredMethods().stream().anyMatch(m -> m.nameEquals("assertInstanceOf"));
    }

    private boolean isAssertionCall(ASTMethodCall call, String methodName) {
        return call.getMethodName().equals(methodName)
            && !call.getOverloadSelectionInfo().isFailed()
            && TestFrameworksUtil.isCallOnAssertionContainer(call);
    }


    private ASTInfixExpression asEqualityExpr(ASTExpression node) {
        if (JavaAstUtils.isInfixExprWithOperator(node, BinaryOp.EQUALITY_OPS)) {
            return (ASTInfixExpression) node;
        }
        return null;
    }

    private boolean isPositiveEqualityExpr(ASTInfixExpression node) {
        return node != null && node.getOperator() == BinaryOp.EQ;
    }

    private static ASTExpression getNegatedExprOperand(ASTExpression node) {
        if (JavaAstUtils.isBooleanNegation(node)) {
            return ((ASTUnaryExpression) node).getOperand();
        }
        return null;
    }
}
