/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

public class ApexParserVisitorAdapter implements ApexParserVisitor {

    /**
     * @deprecated Use {@link #visit(ApexNode, Object)}. That method
     *     also visits comments now.
     */
    @Deprecated
    @Override
    public Object visit(AbstractApexNodeBase node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ApexNode<?> node, Object data) {
        for (ApexNode<?> child : node.children()) {
            child.jjtAccept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTModifierNode node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTParameter node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTBlockStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTUserClassMethods node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTBridgeMethodCreator node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTLiteralExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTConstructorPreambleStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTUserInterface node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTUserEnum node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTWhileLoopStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTTryCatchFinallyBlockStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTForLoopStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTIfElseBlockStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTIfBlockStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTForEachStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTField node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTBreakStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTDoLoopStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTTernaryExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTSoqlExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTBooleanExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTAnnotation node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTAnonymousClass node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTArrayLoadExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTArrayStoreExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTAssignmentExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTBinaryExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTBindExpressions node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTCatchBlockStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTClassRefExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTDmlDeleteStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTDmlInsertStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTDmlMergeStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTDmlUndeleteStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTDmlUpdateStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTDmlUpsertStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTExpressionStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTFieldDeclarationStatements node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTInstanceOfExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTJavaMethodCallExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTJavaVariableExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTMapEntryNode node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTModifierOrAnnotation node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewListInitExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewListLiteralExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewMapInitExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewMapLiteralExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewObjectExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewSetInitExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewSetLiteralExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTPackageVersionExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTPostfixExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTPrefixExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTProperty node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTReferenceExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTRunAsBlockStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTSoslExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTStandardCondition node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTSuperMethodCallExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTSuperVariableExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTThisMethodCallExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTThisVariableExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTTriggerVariableExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTUserExceptionMethods node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTUserTrigger node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTVariableDeclarationStatements node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTVariableExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTAnnotationParameter node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTCastExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTConstructorPreamble node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTIllegalStoreExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTMethodBlockStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTModifier node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTMultiStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNestedExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNestedStoreExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewKeyValueObjectExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTStatementExecuted node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTFormalComment node, Object data) {
        return visit((ApexNode<?>) node, data);
    }
}
