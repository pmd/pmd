/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class PrimitiveWrapperInstantiationRule extends AbstractJavaRulechainRule {

    private static final InvocationMatcher BOOLEAN_VALUEOF_MATCHER = InvocationMatcher.parse("java.lang.Boolean#valueOf(_)");

    public PrimitiveWrapperInstantiationRule() {
        super(ASTConstructorCall.class, ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        ASTClassOrInterfaceType type = node.firstChild(ASTClassOrInterfaceType.class);
        if (type == null) {
            return data;
        }

        if (TypeTestUtil.isA(Double.class, type)
                || TypeTestUtil.isA(Float.class, type)
                || TypeTestUtil.isA(Long.class, type)
                || TypeTestUtil.isA(Integer.class, type)
                || TypeTestUtil.isA(Short.class, type)
                || TypeTestUtil.isA(Byte.class, type)
                || TypeTestUtil.isA(Character.class, type)) {
            addViolation(data, node, type.getSimpleName());
        } else if (TypeTestUtil.isA(Boolean.class, type)) {
            checkArguments(node.getArguments(), node, data);
        }

        return data;
    }

    /**
     * Finds calls of "Boolean.valueOf".
     */
    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (BOOLEAN_VALUEOF_MATCHER.matchesCall(node)) {
            checkArguments(node.getArguments(), node, data);
        }

        return data;
    }

    private void checkArguments(ASTArgumentList arguments, JavaNode node, Object data) {
        if (arguments == null || arguments.size() != 1) {
            return;
        }
        boolean isNewBoolean = node instanceof ASTConstructorCall;
        String messagePart = isNewBoolean
                ? "Do not use `new Boolean"
                : "Do not use `Boolean.valueOf";
        ASTStringLiteral stringLiteral = getFirstArgStringLiteralOrNull(arguments);
        ASTBooleanLiteral boolLiteral = getFirstArgBooleanLiteralOrNull(arguments);
        if (stringLiteral != null) {
            if ("\"true\"".equals(stringLiteral.getImage())) {
                addViolationWithMessage(data, node, messagePart + "(\"true\")`, prefer `Boolean.TRUE`");
            } else if ("\"false\"".equals(stringLiteral.getImage())) {
                addViolationWithMessage(data, node, messagePart + "(\"false\")`, prefer `Boolean.FALSE`");
            } else {
                addViolationWithMessage(data, node, messagePart + "(\"...\")`, prefer `Boolean.valueOf`");
            }
        } else if (boolLiteral != null) {
            if (boolLiteral.isTrue()) {
                addViolationWithMessage(data, node, messagePart + "(true)`, prefer `Boolean.TRUE`");
            } else {
                addViolationWithMessage(data, node, messagePart + "(false)`, prefer `Boolean.FALSE`");
            }
        } else if (isNewBoolean) {
            // any argument with "new Boolean", might be a variable access
            addViolationWithMessage(data, node, messagePart + "(...)`, prefer `Boolean.valueOf`");
        }
    }

    private static ASTStringLiteral getFirstArgStringLiteralOrNull(ASTArgumentList arguments) {
        if (arguments.size() == 1) {
            ASTExpression firstArg = arguments.get(0);
            if (firstArg instanceof ASTStringLiteral) {
                return (ASTStringLiteral) firstArg;
            }
        }
        return null;
    }

    private static ASTBooleanLiteral getFirstArgBooleanLiteralOrNull(ASTArgumentList arguments) {
        if (arguments.size() == 1) {
            ASTExpression firstArg = arguments.get(0);
            if (firstArg instanceof ASTBooleanLiteral) {
                return (ASTBooleanLiteral) firstArg;
            }
        }
        return null;
    }
}
