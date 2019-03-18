/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import javax.annotation.Nonnull;

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
public class JavaParserVisitorAdapter implements JavaParserVisitor {


    public Object visit(ASTExpression node, Object data) {
        return JavaParserVisitor.super.visit(node, data);
    }


    public Object visit(ASTPrimaryExpression node, Object data) {
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

    public Object visit(ASTLiteral node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }

    public Object visit(ASTBooleanLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }

    public Object visit(ASTNullLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }

    public Object visit(ASTNumericLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }

    public Object visit(ASTStringLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }

    public Object visit(ASTCharLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }

    public Object visit(ASTClassLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }
}
