/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import net.sourceforge.pmd.lang.ast.AstVisitor;

public interface EcmascriptVisitor<P, R> extends AstVisitor<P, R> {

    /**
     * Every visit method for other JS nodes forwards to this method
     * by default.
     */
    default R visitJsNode(EcmascriptNode<?> node, P data) {
        return visitNode(node, data);
    }

    default R visit(ASTArrayComprehension node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTArrayComprehensionLoop node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTArrayLiteral node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTAssignment node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTAstRoot node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTBlock node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTBreakStatement node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTCatchClause node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTComment node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTConditionalExpression node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTContinueStatement node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTDoLoop node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTElementGet node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTEmptyExpression node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTEmptyStatement node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTExpressionStatement node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTForInLoop node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTForLoop node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTFunctionCall node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTFunctionNode node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTIfStatement node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTInfixExpression node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTKeywordLiteral node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTLabel node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTLabeledStatement node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTLetNode node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTName node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTNewExpression node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTNumberLiteral node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTObjectLiteral node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTObjectProperty node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTParenthesizedExpression node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTPropertyGet node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTRegExpLiteral node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTReturnStatement node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTScope node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTStringLiteral node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTSwitchCase node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTSwitchStatement node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTThrowStatement node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTTryStatement node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTUnaryExpression node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTVariableDeclaration node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTVariableInitializer node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTWhileLoop node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTWithStatement node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTXmlDotQuery node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTXmlExpression node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTXmlMemberGet node, P data) {
        return visitJsNode(node, data);
    }

    default R visit(ASTXmlString node, P data) {
        return visitJsNode(node, data);
    }
}
