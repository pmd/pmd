/*
 * User: tom
 * Date: Jun 13, 2002
 * Time: 4:57:15 PM
 */
package net.sourceforge.pmd.ast;


public class JavaParserVisitorAdapter implements JavaParserVisitor {
    public Object visit(SimpleNode node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTAssertStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTPackageDeclaration node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTImportDeclaration node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTTypeDeclaration node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTClassDeclaration node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTUnmodifiedClassDeclaration node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTClassBody node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTNestedClassDeclaration node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTClassBodyDeclaration node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTMethodDeclarationLookahead node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTInterfaceDeclaration node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTNestedInterfaceDeclaration node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTUnmodifiedInterfaceDeclaration node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTInterfaceMemberDeclaration node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTVariableDeclarator node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTVariableInitializer node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTArrayInitializer node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTMethodDeclarator node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTFormalParameters node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTFormalParameter node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTConstructorDeclaration node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTExplicitConstructorInvocation node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTInitializer node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTType node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTPrimitiveType node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTResultType node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTName node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTNameList node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTAssignmentOperator node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTConditionalExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTConditionalOrExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTConditionalAndExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTInclusiveOrExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTExclusiveOrExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTAndExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTEqualityExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTInstanceOfExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTRelationalExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTShiftExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTAdditiveExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTMultiplicativeExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTUnaryExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTPreIncrementExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTPreDecrementExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTCastLookahead node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTPostfixExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTCastExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTPrimaryExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTPrimaryPrefix node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTPrimarySuffix node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTLiteral node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTBooleanLiteral node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTNullLiteral node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTArguments node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTArgumentList node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTAllocationExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTArrayDimsAndInits node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTLabeledStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTBlock node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTBlockStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTEmptyStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTStatementExpression node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTSwitchStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTSwitchLabel node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTIfStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTWhileStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTDoStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTForStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTForInit node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTStatementExpressionList node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTForUpdate node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTBreakStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTContinueStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTReturnStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTThrowStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTSynchronizedStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }

    public Object visit(ASTTryStatement node, Object data) {
        node.childrenAccept(this, data);return null;
    }
}
