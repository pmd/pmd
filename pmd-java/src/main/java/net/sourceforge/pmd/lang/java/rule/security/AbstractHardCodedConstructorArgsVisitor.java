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
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

abstract class AbstractHardCodedConstructorArgsVisitor extends AbstractJavaRulechainRule {

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
            }
        } else if (firstArgumentExpression instanceof ASTVariableAccess) {
            // check for named variable
            varAccess = (ASTVariableAccess) firstArgumentExpression;
        }

        if (varAccess != null && varAccess.getSignature() != null && varAccess.getSignature().getSymbol() != null) {
            // named variable or method call on named variable found
            ASTVariableDeclaratorId varDecl = varAccess.getSignature().getSymbol().tryGetNode();
            validateProperKeyArgument(data, varDecl.getInitializer());
            validateVarUsages(data, varDecl);
        } else if (firstArgumentExpression instanceof ASTArrayAllocation) {
            // hard coded array
            ASTArrayInitializer arrayInit = ((ASTArrayAllocation) firstArgumentExpression).getArrayInitializer();
            if (arrayInit != null) {
                addViolation(data, arrayInit);
            }
        } else {
            // string literal
            ASTStringLiteral literal = firstArgumentExpression.descendantsOrSelf()
                    .filterIs(ASTStringLiteral.class).first();
            if (literal != null) {
                addViolation(data, literal);
            }
        }
    }

    private void validateVarUsages(Object data, ASTVariableDeclaratorId varDecl) {
        varDecl.getLocalUsages().stream()
            .filter(u -> u.getAccessType() == AccessType.WRITE)
            .filter(u -> u.getParent() instanceof ASTAssignmentExpression)
            .forEach(usage -> {
                ASTAssignmentExpression assignment = (ASTAssignmentExpression) usage.getParent();
                validateProperKeyArgument(data, assignment.getRightOperand());
            });
    }
}
