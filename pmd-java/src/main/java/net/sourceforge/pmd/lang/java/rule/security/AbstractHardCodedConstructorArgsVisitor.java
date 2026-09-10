/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.security;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

abstract class AbstractHardCodedConstructorArgsVisitor extends AbstractJavaRulechainRule {

    /**
     * Matches calls to {@code System.getProperty(String)} and
     * {@code System.getProperty(String, String)}. Any string literal
     * arguments to such a call (the property name, or the default value
     * used when the property is absent) are not necessarily the actual
     * value used at runtime, since that value may come from an external
     * system property. So these should not be reported as hard-coded.
     */
    private static final InvocationMatcher SYSTEM_GET_PROPERTY =
            InvocationMatcher.parse("java.lang.System#getProperty(_*)");

    private final Class<?> type;

    AbstractHardCodedConstructorArgsVisitor(Class<?> constructorType) {
        super(ASTConstructorCall.class);
        this.type = constructorType;
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        if (TypeTestUtil.isA(type, node)) {
            ASTArgumentList arguments = node.getArguments();
            if (arguments.size() > 0) {
                validateProperKeyArgument(data, arguments.get(0));
            }
        }
        return data;
    }

    /**
     * Recursively resolves the argument again, if the variable initializer
     * is itself a expression.
     *
     * <p>Then checks the expression for being a string literal or array
     */
    private void validateProperKeyArgument(Object data, ASTExpression firstArgumentExpression) {
        if (firstArgumentExpression == null) {
            return;
        }

        ASTVariableAccess varAccess = null;

        if (firstArgumentExpression instanceof ASTMethodCall) {
            // check for method call on a named variable
            ASTExpression expr = ((ASTMethodCall) firstArgumentExpression).getQualifier();
            if (expr instanceof ASTVariableAccess) {
                varAccess = (ASTVariableAccess) expr;
            } else if (expr instanceof ASTMethodCall && SYSTEM_GET_PROPERTY.matchesCall((ASTMethodCall) expr)) {
                // e.g. new SecretKeySpec(System.getProperty("k", "d").getBytes(), "AES")
                // the value comes from an external system property at runtime;
                // any string literal arguments are not necessarily the key
                return;
            }
        } else if (firstArgumentExpression instanceof ASTVariableAccess) {
            // check for named variable
            varAccess = (ASTVariableAccess) firstArgumentExpression;
        }

        if (varAccess != null && varAccess.getSignature() != null && varAccess.getSignature().getSymbol() != null) {
            // named variable or method call on named variable found
            ASTVariableId varDecl = varAccess.getSignature().getSymbol().tryGetNode();
            if (varDecl != null) {
                validateProperKeyArgument(data, varDecl.getInitializer());
                validateVarUsages(data, varDecl);
            } else if (varAccess.isCompileTimeConstant()
                    && varAccess.getConstValue() instanceof String) {
                asCtx(data).addViolation(varAccess);
            }
        } else if (firstArgumentExpression instanceof ASTConstructorCall) {
            ASTConstructorCall constructorCall = (ASTConstructorCall) firstArgumentExpression;
            ASTArgumentList arguments = constructorCall.getArguments();
            if (arguments.size() == 1
                    && TypeTestUtil.isExactlyA(String.class, constructorCall.getTypeNode())
                    && TypeTestUtil.isExactlyA(char[].class, arguments.get(0))) {
                validateHardCodedCharArray(data, arguments.get(0));
            } else {
                addViolationForStringLiteral(data, firstArgumentExpression);
            }
        } else if (firstArgumentExpression instanceof ASTArrayAllocation) {
            // hard coded array
            ASTArrayInitializer arrayInit = ((ASTArrayAllocation) firstArgumentExpression).getArrayInitializer();
            if (arrayInit != null) {
                asCtx(data).addViolation(arrayInit);
            }
        } else if (firstArgumentExpression instanceof ASTArrayInitializer) {
            // hard coded array
            asCtx(data).addViolation(firstArgumentExpression);
        } else if (firstArgumentExpression instanceof ASTMethodCall
                && SYSTEM_GET_PROPERTY.matchesCall((ASTMethodCall) firstArgumentExpression)) {
            // value comes from an external system property at runtime;
            // any string literal arguments are not necessarily the key
        } else {
            // string literal
            addViolationForStringLiteral(data, firstArgumentExpression);
        }
    }

    private void validateHardCodedCharArray(Object data, ASTExpression expression) {
        if (expression instanceof ASTVariableAccess) {
            ASTVariableId varDecl = ((ASTVariableAccess) expression).getReferencedSym().tryGetNode();
            if (varDecl != null) {
                validateHardCodedCharArray(data, varDecl.getInitializer());
            }
        } else if (expression instanceof ASTArrayAllocation) {
            ASTArrayInitializer arrayInit = ((ASTArrayAllocation) expression).getArrayInitializer();
            if (isConstantCharArray(arrayInit)) {
                asCtx(data).addViolation(arrayInit);
            }
        } else if (expression instanceof ASTArrayInitializer && isConstantCharArray((ASTArrayInitializer) expression)) {
            asCtx(data).addViolation(expression);
        }
    }

    private boolean isConstantCharArray(ASTArrayInitializer arrayInit) {
        if (arrayInit == null) {
            return false;
        }
        for (ASTExpression element : arrayInit) {
            if (!(element.getConstValue() instanceof Character)) {
                return false;
            }
        }
        return true;
    }

    private void addViolationForStringLiteral(Object data, ASTExpression expression) {
        ASTStringLiteral literal = expression.descendantsOrSelf()
                .filterIs(ASTStringLiteral.class).first();
        if (literal != null) {
            asCtx(data).addViolation(literal);
        }
    }

    private void validateVarUsages(Object data, ASTVariableId varDecl) {
        varDecl.getLocalUsages().stream()
            .filter(u -> u.getAccessType() == AccessType.WRITE)
            .filter(u -> u.getParent() instanceof ASTAssignmentExpression)
            .forEach(usage -> {
                ASTAssignmentExpression assignment = (ASTAssignmentExpression) usage.getParent();
                validateProperKeyArgument(data, assignment.getRightOperand());
            });
    }
}
