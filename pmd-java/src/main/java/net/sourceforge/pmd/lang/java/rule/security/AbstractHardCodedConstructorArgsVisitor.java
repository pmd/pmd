/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.security;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
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

        // named variable
        if (firstArgumentExpression instanceof ASTVariableAccess) {
            ASTVariableAccess varAccess = (ASTVariableAccess) firstArgumentExpression;
            if (varAccess.getSignature() != null && varAccess.getSignature().getSymbol() != null) {
                ASTVariableDeclaratorId varDecl = varAccess.getSignature().getSymbol().tryGetNode();
                validateProperKeyArgument(data, varDecl.getInitializer());
            }
        }

        // hard coded array
        if (firstArgumentExpression instanceof ASTArrayAllocation) {
            ASTArrayInitializer arrayInit = ((ASTArrayAllocation) firstArgumentExpression).getArrayInitializer();
            if (arrayInit != null) {
                addViolation(data, arrayInit);
            }
        }

        // string literal
        ASTStringLiteral literal = firstArgumentExpression.descendants(ASTStringLiteral.class).first();
        if (literal != null) {
            addViolation(data, literal);
        }
    }
}
