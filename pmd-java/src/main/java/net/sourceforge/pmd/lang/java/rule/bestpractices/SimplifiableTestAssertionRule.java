/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.isBooleanLiteral;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;

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
        final boolean isAssertTrue = isAssertionCall(node, "assertTrue");
        final boolean isAssertFalse = isAssertionCall(node, "assertFalse");

        if (isAssertTrue || isAssertFalse) {
            ASTArgumentList args = node.getArguments();
            ASTExpression lastArg = args.getLastChild();
            ASTInfixExpression eq = asEqualityExpr(lastArg);
            if (eq != null) {
                boolean isPositive = isPositiveEqualityExpr(eq) == isAssertTrue;
                final String suggestion;
                if (JavaRuleUtil.isNullLiteral(eq.getLeftOperand())
                    || JavaRuleUtil.isNullLiteral(eq.getRightOperand())) {
                    // use assertNull/assertNonNull
                    suggestion = isPositive ? "assertNull" : "assertNonNull";
                } else {
                    if (isPrimitive(eq.getLeftOperand()) || isPrimitive(eq.getRightOperand())) {
                        suggestion = isPositive ? "assertEquals" : "assertNotEquals";
                    } else {
                        suggestion = isPositive ? "assertSame" : "assertNotSame";
                    }
                }
                addViolation(data, node, suggestion);

            } else {
                @Nullable ASTExpression negatedExprOperand = getNegatedExprOperand(lastArg);

                if (OBJECT_EQUALS.matchesCall(negatedExprOperand)) {
                    //assertTrue(!a.equals(b))
                    String suggestion = isAssertTrue ? "assertNotEquals" : "assertEquals";
                    addViolation(data, node, suggestion);

                } else if (negatedExprOperand != null) {
                    //assertTrue(!something)
                    String suggestion = isAssertTrue ? "assertFalse" : "assertTrue";
                    addViolation(data, node, suggestion);

                } else if (OBJECT_EQUALS.matchesCall(lastArg)) {
                    //assertTrue(a.equals(b))
                    String suggestion = isAssertTrue ? "assertEquals" : "assertNotEquals";
                    addViolation(data, node, suggestion);
                }
            }
        }

        boolean isAssertEquals = isAssertionCall(node, "assertEquals");
        boolean isAssertNotEquals = isAssertionCall(node, "assertNotEquals");

        if (isAssertEquals || isAssertNotEquals) {
            ASTArgumentList argList = node.getArguments();
            if (argList.size() >= 2) {
                ASTExpression comp0 = getChildRev(argList, -1);
                ASTExpression comp1 = getChildRev(argList, -2);
                if (isBooleanLiteral(comp0) ^ isBooleanLiteral(comp1)) {
                    if (isBooleanLiteral(comp1)) {
                        ASTExpression tmp = comp0;
                        comp0 = comp1;
                        comp1 = tmp;
                    }
                    // now the literal is in comp0 and the other is some expr
                    if (comp1.getTypeMirror().isPrimitive(PrimitiveTypeKind.BOOLEAN)) {
                        ASTBooleanLiteral literal = (ASTBooleanLiteral) comp0;
                        String suggestion = literal.isTrue() == isAssertEquals ? "assertTrue" : "assertFalse";
                        addViolation(data, node, suggestion);
                    }
                }
            }
        }

        return null;
    }

    private boolean isPrimitive(ASTExpression node) {
        return node.getTypeMirror().isPrimitive();
    }

    /**
     * Returns a child with an offset from the end. Eg {@code getChildRev(list, -1)}
     * returns the last child.
     */
    private static <T extends JavaNode> T getChildRev(@NonNull ASTList<T> list, int i) {
        assert i < 0 : "Expecting negative offset";
        return list.get(list.getNumChildren() + i);
    }

    private boolean isAssertionCall(ASTMethodCall call, String methodName) {
        return call.getMethodName().equals(methodName)
            && !call.getOverloadSelectionInfo().isFailed()
            && TestFrameworksUtil.isCallOnAssertionContainer(call);
    }


    private ASTInfixExpression asEqualityExpr(ASTExpression node) {
        if (BinaryOp.isInfixExprWithOperator(node, BinaryOp.EQUALITY_OPS)) {
            return (ASTInfixExpression) node;
        }
        return null;
    }

    private boolean isPositiveEqualityExpr(ASTInfixExpression node) {
        return node != null && node.getOperator() == BinaryOp.EQ;
    }

    private static ASTExpression getNegatedExprOperand(ASTExpression node) {
        if (JavaRuleUtil.isBooleanNegation(node)) {
            return ((ASTUnaryExpression) node).getOperand();
        }
        return null;
    }
}
