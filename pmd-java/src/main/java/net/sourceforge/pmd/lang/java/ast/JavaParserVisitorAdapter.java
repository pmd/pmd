/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import javax.annotation.Nonnull;

import net.sourceforge.pmd.annotation.UnknownNullabilityApi;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;


/**
 * An adapter for {@link JavaParserVisitor}. Unless visit methods are overridden without
 * calling {@code super.visit}, the visitor performs a full depth-first tree walk.
 *
 * <p>Since 7.0.0 we use default methods
 * on the interface, which removes code duplication. However, if a visitor directly
 * implements the interface, then the syntax {@code super.visit(...)} is illegal and
 * doesn't refer to the default method. Instead, one would have to qualify the super,
 * like {@code JavaParserVisitor.super.visit}.
 *
 * <p>This restriction doesn't apply when the interface is not a direct super interface,
 * i.e. when there's an intermediary class like this one in the type hierarchy, or
 * e.g. {@link AbstractJavaRule}. That's why extending this class is preferred to
 * implementing the visitor directly.
 */
@UnknownNullabilityApi
public class JavaParserVisitorAdapter implements JavaParserVisitor {


    public Object visit(@Nonnull ASTType node, Object data) {
        return JavaParserVisitor.super.visit(node, data);
    }


    @Override
    public Object visit(@Nonnull ASTPrimitiveType node, Object data) {
        return visit((ASTType) node, data);
    }

    public Object visit(@Nonnull ASTReferenceType node, Object data) {
        return visit((ASTType) node, data);
    }


    @Override
    public Object visit(@Nonnull ASTArrayType node, Object data) {
        return visit((ASTReferenceType) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTClassOrInterfaceType node, Object data) {
        return visit((ASTReferenceType) node, data);
    }


    public Object visit(@Nonnull ASTExpression node, Object data) {
        return JavaParserVisitor.super.visit(node, data);
    }

    @Override
    public Object visit(@Nonnull ASTAssignmentExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTConditionalExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTConditionalOrExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTConditionalAndExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTInclusiveOrExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTExclusiveOrExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTAndExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTEqualityExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTRelationalExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTInstanceOfExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTShiftExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTAdditiveExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTMultiplicativeExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTUnaryExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTPreIncrementExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTPreDecrementExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTCastExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTPostfixExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }


    public Object visit(@Nonnull ASTPrimaryExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }


    @Override
    public Object visit(@Nonnull ASTMethodCall node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTFieldAccess node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTConstructorCall node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }


    @Override
    public Object visit(@Nonnull ASTArrayAllocation node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }


    @Override
    public Object visit(@Nonnull ASTArrayAccess node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }


    @Override
    public Object visit(@Nonnull ASTVariableReference node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }


    @Override
    public Object visit(@Nonnull ASTParenthesizedExpression node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }


    @Override
    public Object visit(@Nonnull ASTMethodReference node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }


    @Override
    public Object visit(@Nonnull ASTThisExpression node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTSuperExpression node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }

    public Object visit(@Nonnull ASTLiteral node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTBooleanLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTNullLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTNumericLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTStringLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTCharLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }

    @Override
    public Object visit(@Nonnull ASTClassLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }
}
