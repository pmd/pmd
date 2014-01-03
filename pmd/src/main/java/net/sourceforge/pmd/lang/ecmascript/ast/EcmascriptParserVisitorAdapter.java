/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

public class EcmascriptParserVisitorAdapter implements EcmascriptParserVisitor {

    public Object visit(EcmascriptNode node, Object data) {
	node.childrenAccept(this, data);
	return null;
    }

    public Object visit(ASTArrayComprehension node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTArrayComprehensionLoop node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTArrayLiteral node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTAssignment node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTAstRoot node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTBlock node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTBreakStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTCatchClause node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTComment node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTConditionalExpression node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTContinueStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTDoLoop node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTElementGet node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTEmptyExpression node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTExpressionStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTForInLoop node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTForLoop node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTFunctionCall node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTFunctionNode node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTIfStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTInfixExpression node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTKeywordLiteral node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTLabel node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTLabeledStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTLetNode node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTName node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTNewExpression node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTNumberLiteral node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTObjectLiteral node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTObjectProperty node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTParenthesizedExpression node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTPropertyGet node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTRegExpLiteral node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTReturnStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTScope node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTStringLiteral node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTSwitchCase node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTSwitchStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTThrowStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTTryStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTUnaryExpression node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTVariableDeclaration node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTVariableInitializer node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTWhileLoop node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTWithStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTXmlDotQuery node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTXmlExpression node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTXmlMemberGet node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTXmlString node, Object data) {
	return visit((EcmascriptNode) node, data);
    }
}
