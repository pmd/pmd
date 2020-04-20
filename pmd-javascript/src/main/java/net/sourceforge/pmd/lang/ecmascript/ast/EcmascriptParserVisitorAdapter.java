/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

public class EcmascriptParserVisitorAdapter implements EcmascriptParserVisitor {

    @Override
    public Object visit(EcmascriptNode<?> node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTArrayComprehension node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTArrayComprehensionLoop node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTArrayLiteral node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTAssignment node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTAstRoot node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTBreakStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTCatchClause node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTComment node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTDoLoop node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTElementGet node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTEmptyExpression node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTExpressionStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTForInLoop node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTForLoop node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTFunctionCall node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTFunctionNode node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTInfixExpression node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTKeywordLiteral node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTLabel node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTLabeledStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTLetNode node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTName node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewExpression node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNumberLiteral node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTObjectLiteral node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTObjectProperty node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTParenthesizedExpression node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTPropertyGet node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTRegExpLiteral node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTScope node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTStringLiteral node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTSwitchCase node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTTryStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTUnaryExpression node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTVariableInitializer node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTWhileLoop node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTWithStatement node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTXmlDotQuery node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTXmlExpression node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTXmlMemberGet node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }

    @Override
    public Object visit(ASTXmlString node, Object data) {
        return visit((EcmascriptNode<?>) node, data);
    }
}
