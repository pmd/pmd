/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.AstVisitor;

public interface ApexVisitor<P, R> extends AstVisitor<P, R> {

    /**
     * The default visit method, to which other methods delegate.
     */
    default R visitApexNode(ApexNode<?> node, P data) {
        return visitNode(node, data);
    }


    default R visit(ASTApexFile node, P data) {
        return visitNode(node, data);
    }

    default R visit(ASTAnnotation node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTAnnotationParameter node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTAnonymousClass node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTArrayLoadExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTArrayStoreExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTAssignmentExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTBinaryExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTBindExpressions node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTBlockStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTBooleanExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTBreakStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTBridgeMethodCreator node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTCastExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTCatchBlockStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTClassRefExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTConstructorPreamble node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTConstructorPreambleStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTContinueStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTDmlDeleteStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTDmlInsertStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTDmlMergeStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTDmlUndeleteStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTDmlUpdateStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTDmlUpsertStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTDoLoopStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTExpressionStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTField node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTFieldDeclaration node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTFieldDeclarationStatements node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTFormalComment node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTForEachStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTForLoopStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTIfBlockStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTIfElseBlockStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTIllegalStoreExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTInstanceOfExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTJavaMethodCallExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTJavaVariableExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTLiteralExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTMapEntryNode node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTMethod node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTMethodBlockStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTMethodCallExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTModifier node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTModifierNode node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTModifierOrAnnotation node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTMultiStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTNestedExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTNestedStoreExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTNewKeyValueObjectExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTNewListInitExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTNewListLiteralExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTNewMapInitExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTNewMapLiteralExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTNewObjectExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTNewSetInitExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTNewSetLiteralExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTPackageVersionExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTParameter node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTPostfixExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTPrefixExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTProperty node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTReferenceExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTReturnStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTRunAsBlockStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTSoqlExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTSoslExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTStandardCondition node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTStatementExecuted node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTSuperMethodCallExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTSuperVariableExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTTernaryExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTThisMethodCallExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTThisVariableExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTThrowStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTTriggerVariableExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTTryCatchFinallyBlockStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTUserClass node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTUserClassMethods node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTUserEnum node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTUserExceptionMethods node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTUserInterface node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTUserTrigger node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTVariableDeclaration node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTVariableDeclarationStatements node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTVariableExpression node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTWhileLoopStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTSwitchStatement node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTElseWhenBlock node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTTypeWhenBlock node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTValueWhenBlock node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTLiteralCase node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTIdentifierCase node, P data) {
        return visitApexNode(node, data);
    }

    default R visit(ASTEmptyReferenceExpression node, P data) {
        return visitApexNode(node, data);
    }
}
