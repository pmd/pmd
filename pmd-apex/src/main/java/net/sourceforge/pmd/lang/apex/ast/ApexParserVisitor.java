/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

public interface ApexParserVisitor {
    /**
     * @deprecated Use {@link #visit(ApexNode, Object)}. That method
     *     also visits comments now.
     */
    @Deprecated
    Object visit(AbstractApexNodeBase node, Object data);

    Object visit(ApexNode<?> node, Object data);

    Object visit(ASTAnnotation node, Object data);

    Object visit(ASTAnnotationParameter node, Object data);

    Object visit(ASTAnonymousClass node, Object data);

    Object visit(ASTArrayLoadExpression node, Object data);

    Object visit(ASTArrayStoreExpression node, Object data);

    Object visit(ASTAssignmentExpression node, Object data);

    Object visit(ASTBinaryExpression node, Object data);

    Object visit(ASTBindExpressions node, Object data);

    Object visit(ASTBlockStatement node, Object data);

    Object visit(ASTBooleanExpression node, Object data);

    Object visit(ASTBreakStatement node, Object data);

    Object visit(ASTBridgeMethodCreator node, Object data);

    Object visit(ASTCastExpression node, Object data);

    Object visit(ASTCatchBlockStatement node, Object data);

    Object visit(ASTClassRefExpression node, Object data);

    Object visit(ASTConstructorPreamble node, Object data);

    Object visit(ASTConstructorPreambleStatement node, Object data);

    Object visit(ASTContinueStatement node, Object data);

    Object visit(ASTDmlDeleteStatement node, Object data);

    Object visit(ASTDmlInsertStatement node, Object data);

    Object visit(ASTDmlMergeStatement node, Object data);

    Object visit(ASTDmlUndeleteStatement node, Object data);

    Object visit(ASTDmlUpdateStatement node, Object data);

    Object visit(ASTDmlUpsertStatement node, Object data);

    Object visit(ASTDoLoopStatement node, Object data);

    Object visit(ASTExpression node, Object data);

    Object visit(ASTExpressionStatement node, Object data);

    Object visit(ASTField node, Object data);

    Object visit(ASTFieldDeclaration node, Object data);

    Object visit(ASTFieldDeclarationStatements node, Object data);

    Object visit(ASTFormalComment node, Object data);

    Object visit(ASTForEachStatement node, Object data);

    Object visit(ASTForLoopStatement node, Object data);

    Object visit(ASTIfBlockStatement node, Object data);

    Object visit(ASTIfElseBlockStatement node, Object data);

    Object visit(ASTIllegalStoreExpression node, Object data);

    Object visit(ASTInstanceOfExpression node, Object data);

    Object visit(ASTJavaMethodCallExpression node, Object data);

    Object visit(ASTJavaVariableExpression node, Object data);

    Object visit(ASTLiteralExpression node, Object data);

    Object visit(ASTMapEntryNode node, Object data);

    Object visit(ASTMethod node, Object data);

    Object visit(ASTMethodBlockStatement node, Object data);

    Object visit(ASTMethodCallExpression node, Object data);

    Object visit(ASTModifier node, Object data);

    Object visit(ASTModifierNode node, Object data);

    Object visit(ASTModifierOrAnnotation node, Object data);

    Object visit(ASTMultiStatement node, Object data);

    Object visit(ASTNestedExpression node, Object data);

    Object visit(ASTNestedStoreExpression node, Object data);

    Object visit(ASTNewKeyValueObjectExpression node, Object data);

    Object visit(ASTNewListInitExpression node, Object data);

    Object visit(ASTNewListLiteralExpression node, Object data);

    Object visit(ASTNewMapInitExpression node, Object data);

    Object visit(ASTNewMapLiteralExpression node, Object data);

    Object visit(ASTNewObjectExpression node, Object data);

    Object visit(ASTNewSetInitExpression node, Object data);

    Object visit(ASTNewSetLiteralExpression node, Object data);

    Object visit(ASTPackageVersionExpression node, Object data);

    Object visit(ASTParameter node, Object data);

    Object visit(ASTPostfixExpression node, Object data);

    Object visit(ASTPrefixExpression node, Object data);

    Object visit(ASTProperty node, Object data);

    Object visit(ASTReferenceExpression node, Object data);

    Object visit(ASTReturnStatement node, Object data);

    Object visit(ASTRunAsBlockStatement node, Object data);

    Object visit(ASTSoqlExpression node, Object data);

    Object visit(ASTSoslExpression node, Object data);

    Object visit(ASTStandardCondition node, Object data);

    Object visit(ASTStatement node, Object data);

    Object visit(ASTStatementExecuted node, Object data);

    Object visit(ASTSuperMethodCallExpression node, Object data);

    Object visit(ASTSuperVariableExpression node, Object data);

    Object visit(ASTTernaryExpression node, Object data);

    Object visit(ASTThisMethodCallExpression node, Object data);

    Object visit(ASTThisVariableExpression node, Object data);

    Object visit(ASTThrowStatement node, Object data);

    Object visit(ASTTriggerVariableExpression node, Object data);

    Object visit(ASTTryCatchFinallyBlockStatement node, Object data);

    Object visit(ASTUserClass node, Object data);

    Object visit(ASTUserClassMethods node, Object data);

    Object visit(ASTUserEnum node, Object data);

    Object visit(ASTUserExceptionMethods node, Object data);

    Object visit(ASTUserInterface node, Object data);

    Object visit(ASTUserTrigger node, Object data);

    Object visit(ASTVariableDeclaration node, Object data);

    Object visit(ASTVariableDeclarationStatements node, Object data);

    Object visit(ASTVariableExpression node, Object data);

    Object visit(ASTWhileLoopStatement node, Object data);
}
