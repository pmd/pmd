package net.sourceforge.pmd.ast;

public class JavaParserVisitorAdapter implements JavaParserVisitor {
    public Object visit(SimpleNode node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTEnumDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTEnumElement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTAssertStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTPackageDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTImportDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTTypeDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTClassDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTUnmodifiedClassDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTClassBody node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTNestedClassDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTClassBodyDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTMethodDeclarationLookahead node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTNestedInterfaceDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTUnmodifiedInterfaceDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTInterfaceMemberDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTVariableDeclarator node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTVariableInitializer node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTArrayInitializer node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTMethodDeclarator node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTFormalParameters node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTFormalParameter node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTConstructorDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTExplicitConstructorInvocation node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTInitializer node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTType node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTPrimitiveType node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTResultType node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTName node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTNameList node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTAssignmentOperator node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTConditionalExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTConditionalOrExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTConditionalAndExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTInclusiveOrExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTExclusiveOrExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTAndExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTEqualityExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTInstanceOfExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTRelationalExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTShiftExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTAdditiveExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTMultiplicativeExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTUnaryExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTPreIncrementExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTPreDecrementExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTCastLookahead node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTPostfixExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTCastExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTPrimaryExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTPrimaryPrefix node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTPrimarySuffix node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTLiteral node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTBooleanLiteral node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTNullLiteral node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTArguments node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTArgumentList node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTAllocationExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTArrayDimsAndInits node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTLabeledStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTBlock node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTBlockStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTEmptyStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTStatementExpression node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTSwitchStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTSwitchLabel node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTIfStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTWhileStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTDoStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTForStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTForInit node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTStatementExpressionList node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTForUpdate node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTBreakStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTContinueStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTReturnStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTThrowStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTSynchronizedStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }

    public Object visit(ASTTryStatement node, Object data) {
        return visit((SimpleNode) node, data);
    }
}
