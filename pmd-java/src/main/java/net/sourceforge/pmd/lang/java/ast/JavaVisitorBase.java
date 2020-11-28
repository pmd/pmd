/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.AstVisitorBase;

/**
 * Base implementation of {@link JavaVisitor}. This adds delegation logic
 * which the interface doesn't have.
 *
 * <p>Contrary to the old visitor, which used Object as both parameter and
 * return type, this visitor uses separate type parameters for those. This
 * means you can't just return the parameter, unless your visitor has equal
 * parameter and return type. This type signature subsumes many possible
 * signatures. The old one is {@code <Object, Object>}, still implemented
 * by {@link JavaParserVisitor} for backwards compatibility. If you don't
 * want to return a value, or don't want a parameter, use {@link Void}.
 *
 * <p>Since 7.0.0 we use default methods on the interface, which removes
 * code duplication. However it's still recommended to extend a base class,
 * for forward compatibility.
 */
public class JavaVisitorBase<P, R> extends AstVisitorBase<P, R> implements JavaVisitor<P, R> {


    // <editor-fold defaultstate="collapsed" desc="Methods/constructors">


    public R visitMethodOrCtor(ASTMethodOrConstructorDeclaration node, P data) {
        return visitJavaNode(node, data);
    }

    @Override
    public R visit(ASTMethodDeclaration node, P data) {
        return visitMethodOrCtor(node, data);
    }

    @Override
    public R visit(ASTConstructorDeclaration node, P data) {
        return visitMethodOrCtor(node, data);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Type declarations">

    public R visitTypeDecl(ASTAnyTypeDeclaration node, P data) {
        return visitJavaNode(node, data);
    }

    @Override
    public R visit(ASTClassOrInterfaceDeclaration node, P data) {
        return visitTypeDecl(node, data);
    }

    @Override
    public R visit(ASTAnonymousClassDeclaration node, P data) {
        return visitTypeDecl(node, data);
    }

    @Override
    public R visit(ASTRecordDeclaration node, P data) {
        return visitTypeDecl(node, data);
    }

    @Override
    public R visit(ASTEnumDeclaration node, P data) {
        return visitTypeDecl(node, data);
    }

    @Override
    public R visit(ASTAnnotationTypeDeclaration node, P data) {
        return visitTypeDecl(node, data);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Type & ReferenceType">


    /** Note that VoidType does not delegate to here. */
    public R visitType(ASTType node, P data) {
        return visitJavaNode(node, data);
    }

    @Override
    public R visit(ASTPrimitiveType node, P data) {
        return visitType(node, data);
    }

    public R visitReferenceType(ASTReferenceType node, P data) {
        return visitType(node, data);
    }


    @Override
    public R visit(ASTArrayType node, P data) {
        return visitReferenceType(node, data);
    }


    @Override
    public R visit(ASTIntersectionType node, P data) {
        return visitReferenceType(node, data);
    }


    @Override
    public R visit(ASTWildcardType node, P data) {
        return visitReferenceType(node, data);
    }

    @Override
    public R visit(ASTUnionType node, P data) {
        return visitReferenceType(node, data);
    }

    @Override
    public R visit(ASTClassOrInterfaceType node, P data) {
        return visitReferenceType(node, data);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Expressions">


    public R visitExpression(ASTExpression node, P data) {
        return visitJavaNode(node, data);
    }

    @Override
    public R visit(ASTLambdaExpression node, P data) {
        return visitExpression(node, data);
    }

    @Override
    public R visit(ASTAssignmentExpression node, P data) {
        return visitExpression(node, data);
    }

    @Override
    public R visit(ASTConditionalExpression node, P data) {
        return visitExpression(node, data);
    }


    @Override
    public R visit(ASTInfixExpression node, P data) {
        return visitExpression(node, data);
    }


    @Override
    public R visit(ASTUnaryExpression node, P data) {
        return visitExpression(node, data);
    }

    @Override
    public R visit(ASTCastExpression node, P data) {
        return visitExpression(node, data);
    }


    @Override
    public R visit(ASTSwitchExpression node, P data) {
        return visitExpression(node, data);
    }


    /*
        Primaries
     */


    public R visitPrimaryExpr(ASTPrimaryExpression node, P data) {
        return visitExpression(node, data);
    }


    @Override
    public R visit(ASTMethodCall node, P data) {
        return visitPrimaryExpr(node, data);
    }

    @Override
    public R visit(ASTFieldAccess node, P data) {
        return visitPrimaryExpr(node, data);
    }

    @Override
    public R visit(ASTConstructorCall node, P data) {
        return visitPrimaryExpr(node, data);
    }


    @Override
    public R visit(ASTArrayAllocation node, P data) {
        return visitPrimaryExpr(node, data);
    }


    @Override
    public R visit(ASTArrayAccess node, P data) {
        return visitPrimaryExpr(node, data);
    }


    @Override
    public R visit(ASTVariableAccess node, P data) {
        return visitPrimaryExpr(node, data);
    }


    @Override
    public R visit(ASTMethodReference node, P data) {
        return visitPrimaryExpr(node, data);
    }


    @Override
    public R visit(ASTThisExpression node, P data) {
        return visitPrimaryExpr(node, data);
    }

    @Override
    public R visit(ASTSuperExpression node, P data) {
        return visitPrimaryExpr(node, data);
    }

    @Override
    public R visit(ASTClassLiteral node, P data) {
        return visitPrimaryExpr(node, data);
    }

    /*
        Literals
     */

    public R visitLiteral(ASTLiteral node, P data) {
        return visitPrimaryExpr(node, data);
    }

    @Override
    public R visit(ASTBooleanLiteral node, P data) {
        return visitLiteral(node, data);
    }

    @Override
    public R visit(ASTNullLiteral node, P data) {
        return visitLiteral(node, data);
    }

    @Override
    public R visit(ASTNumericLiteral node, P data) {
        return visitLiteral(node, data);
    }

    @Override
    public R visit(ASTStringLiteral node, P data) {
        return visitLiteral(node, data);
    }

    @Override
    public R visit(ASTCharLiteral node, P data) {
        return visitLiteral(node, data);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Statements">


    public R visitStatement(ASTStatement node, P data) {
        return visitJavaNode(node, data);
    }

    @Override
    public R visit(ASTAssertStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTBlock node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTBreakStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTContinueStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTDoStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTEmptyStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTExplicitConstructorInvocation node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTExpressionStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTForeachStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTForStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTIfStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTLabeledStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTLocalClassStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTLocalVariableDeclaration node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTReturnStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTStatementExpressionList node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTSwitchStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTSynchronizedStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTThrowStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTTryStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTWhileStatement node, P data) {
        return visitStatement(node, data);
    }

    @Override
    public R visit(ASTYieldStatement node, P data) {
        return visitStatement(node, data);
    }



    // </editor-fold>


}
