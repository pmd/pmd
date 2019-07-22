/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

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


    public Object visit(ASTAnnotation node, Object data) {
        return JavaParserVisitor.super.visit(node, data);
    }

    @Override
    public Object visit(ASTMarkerAnnotation node, Object data) {
        return visit((ASTAnnotation) node, data);
    }

    @Override
    public Object visit(ASTSingleMemberAnnotation node, Object data) {
        return visit((ASTAnnotation) node, data);
    }

    @Override
    public Object visit(ASTNormalAnnotation node, Object data) {
        return visit((ASTAnnotation) node, data);
    }

    public Object visit(ASTType node, Object data) {
        return JavaParserVisitor.super.visit(node, data);
    }

    @Override
    public Object visit(ASTPrimitiveType node, Object data) {
        return visit((ASTType) node, data);
    }

    public Object visit(ASTReferenceType node, Object data) {
        return visit((ASTType) node, data);
    }


    @Override
    public Object visit(ASTArrayType node, Object data) {
        return visit((ASTReferenceType) node, data);
    }


    @Override
    public Object visit(ASTIntersectionType node, Object data) {
        return visit((ASTReferenceType) node, data);
    }


    @Override
    public Object visit(ASTWildcardType node, Object data) {
        return visit((ASTReferenceType) node, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        return visit((ASTReferenceType) node, data);
    }


    public Object visit(ASTExpression node, Object data) {
        return JavaParserVisitor.super.visit(node, data);
    }

    @Override
    public Object visit(ASTLambdaExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(ASTAssignmentExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(ASTConditionalOrExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(ASTConditionalAndExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(ASTInclusiveOrExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(ASTExclusiveOrExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(ASTAndExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(ASTEqualityExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(ASTRelationalExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(ASTInstanceOfExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(ASTShiftExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(ASTAdditiveExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(ASTMultiplicativeExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(ASTUnaryExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Override
    public Object visit(ASTCastExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }


    @Override
    public Object visit(ASTSwitchExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }


    public Object visit(ASTPrimaryExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }


    @Override
    public Object visit(ASTMethodCall node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }

    @Override
    public Object visit(ASTFieldAccess node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }


    @Override
    public Object visit(ASTArrayAllocation node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }


    @Override
    public Object visit(ASTArrayAccess node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }


    @Override
    public Object visit(ASTVariableAccess node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }


    @Override
    public Object visit(ASTMethodReference node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }


    @Override
    public Object visit(ASTThisExpression node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }

    @Override
    public Object visit(ASTSuperExpression node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }

    @Override
    public Object visit(ASTClassLiteral node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }

    public Object visit(ASTLiteral node, Object data) {
        return visit((ASTPrimaryExpression) node, data);
    }

    @Override
    public Object visit(ASTBooleanLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }

    @Override
    public Object visit(ASTNullLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }

    @Override
    public Object visit(ASTNumericLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }

    @Override
    public Object visit(ASTStringLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }

    @Override
    public Object visit(ASTCharLiteral node, Object data) {
        return visit((ASTLiteral) node, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }


    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }


    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }


    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        return visit((ASTMethodOrConstructorDeclaration) node, data);
    }

    @Override
    public Object visit(ASTAnnotationMethodDeclaration node, Object data) {
        return visit((ASTMethodOrConstructorDeclaration) node, data);
    }


    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        return visit((ASTMethodOrConstructorDeclaration) node, data);
    }


    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        return visit((MethodLikeNode) node, data);
    }


    // REMOVE ME
    // deprecated stuff kept for compatibility with existing visitors, not matched by anything

    @Deprecated
    public Object visit(ASTArguments node, Object data) {
        return null;
    }

    @Deprecated
    public Object visit(ASTAllocationExpression node, Object data) {
        return null;
    }

    @Deprecated
    public Object visit(ASTTypeArgument node, Object data) {
        return null;
    }

    @Deprecated
    public Object visit(ASTWildcardBounds node, Object data) {
        return null;
    }

    @Deprecated
    public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
        return null;
    }

    @Deprecated
    public Object visit(ASTMethodDeclarator node, Object data) {
        return null;
    }
}
