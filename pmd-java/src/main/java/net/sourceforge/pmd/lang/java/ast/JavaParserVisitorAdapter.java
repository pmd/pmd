/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * An adapter for {@link JavaParserVisitor}.
 *
 * @deprecated Use {@link JavaVisitorBase}
 */
@Deprecated
@DeprecatedUntil700
public class JavaParserVisitorAdapter extends JavaVisitorBase<Object, Object> implements JavaParserVisitor {

    @Override
    protected Object visitChildren(Node node, Object data) {
        super.visitChildren(node, data);
        return data;
    }


    @Override
    public Object visit(ASTType node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTPrimitiveType node, Object data) {
        return visit((ASTType) node, data);
    }

    @Override
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


    @Override
    public Object visit(ASTExpression node, Object data) {
        return visit((JavaNode) node, data);
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
    public Object visit(ASTInfixExpression node, Object data) {
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


    @Override
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

    @Override
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
    public Object visit(ASTAnonymousClassDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }

    @Override
    public Object visit(ASTRecordDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        return visit((ASTMethodOrConstructorDeclaration) node, data);
    }



    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        return visit((ASTMethodOrConstructorDeclaration) node, data);
    }


    @Override
    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        return visit((MethodLikeNode) node, data);
    }

    @Override
    public Object visit(ASTAssertStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTBreakStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTDoStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTEmptyStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTExplicitConstructorInvocation node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTExpressionStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTForeachStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTLabeledStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTLocalClassStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTStatementExpressionList node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTSynchronizedStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTTryStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTYieldStatement node, Object data) {
        return visit((ASTStatement) node, data);
    }

    @Override
    public Object visit(ASTStatement node, Object data) {
        return visit((JavaNode) node, data);
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
    public Object visit(ASTConditionalOrExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTConditionalAndExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTInclusiveOrExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTExclusiveOrExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTAndExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTEqualityExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTRelationalExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTShiftExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTAdditiveExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTMultiplicativeExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTInstanceOfExpression node, Object data) {
        return visit((ASTExpression) node, data);
    }

    @Deprecated
    public Object visit(ASTMethodDeclarator node, Object data) {
        return null;
    }

    @Deprecated
    public Object visit(ASTAnnotationMethodDeclaration node, Object data) {
        return null;
    }

    @Deprecated
    public Object visit(ASTBlockStatement node, Object data) {
        return null;
    }


    @Deprecated
    public Object visit(ASTClassOrInterfaceBodyDeclaration node, Object data) {
        return null;
    }

    @Deprecated
    public Object visit(ASTStatementExpression node, Object data) {
        return null;
    }

}
