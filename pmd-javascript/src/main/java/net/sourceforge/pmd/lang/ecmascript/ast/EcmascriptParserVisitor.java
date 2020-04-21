/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

public interface EcmascriptParserVisitor {

    default Object visit(EcmascriptNode<?> node, Object data) {
        for (EcmascriptNode<?> child : node.children()) {
            child.jjtAccept(this, data);
        }
        return data;
    }

    default Object visit(ASTArrayComprehension node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTArrayComprehensionLoop node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTArrayLiteral node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTAssignment node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTAstRoot node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTBlock node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTBreakStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTCatchClause node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTComment node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTConditionalExpression node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTContinueStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTDoLoop node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTElementGet node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTEmptyExpression node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTExpressionStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTForInLoop node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTForLoop node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTFunctionCall node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTFunctionNode node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTIfStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTInfixExpression node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTKeywordLiteral node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTLabel node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTLabeledStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTLetNode node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTName node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTNewExpression node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTNumberLiteral node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTObjectLiteral node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTObjectProperty node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTParenthesizedExpression node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTPropertyGet node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTRegExpLiteral node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTReturnStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTScope node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTStringLiteral node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTSwitchCase node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTSwitchStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTThrowStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTTryStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTUnaryExpression node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTVariableDeclaration node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTVariableInitializer node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTWhileLoop node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTWithStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTXmlDotQuery node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTXmlExpression node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTXmlMemberGet node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    default Object visit(ASTXmlString node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }
}
