/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

public interface EcmascriptParserVisitor {
    Object visit(EcmascriptNode<?> node, Object data);

    Object visit(ASTArrayComprehension node, Object data);

    Object visit(ASTArrayComprehensionLoop node, Object data);

    Object visit(ASTArrayLiteral node, Object data);

    Object visit(ASTAssignment node, Object data);

    Object visit(ASTAstRoot node, Object data);

    Object visit(ASTBlock node, Object data);

    Object visit(ASTBreakStatement node, Object data);

    Object visit(ASTCatchClause node, Object data);

    Object visit(ASTComment node, Object data);

    Object visit(ASTConditionalExpression node, Object data);

    Object visit(ASTContinueStatement node, Object data);

    Object visit(ASTDoLoop node, Object data);

    Object visit(ASTElementGet node, Object data);

    Object visit(ASTEmptyExpression node, Object data);

    Object visit(ASTExpressionStatement node, Object data);

    Object visit(ASTForInLoop node, Object data);

    Object visit(ASTForLoop node, Object data);

    Object visit(ASTFunctionCall node, Object data);

    Object visit(ASTFunctionNode node, Object data);

    Object visit(ASTIfStatement node, Object data);

    Object visit(ASTInfixExpression node, Object data);

    Object visit(ASTKeywordLiteral node, Object data);

    Object visit(ASTLabel node, Object data);

    Object visit(ASTLabeledStatement node, Object data);

    Object visit(ASTLetNode node, Object data);

    Object visit(ASTName node, Object data);

    Object visit(ASTNewExpression node, Object data);

    Object visit(ASTNumberLiteral node, Object data);

    Object visit(ASTObjectLiteral node, Object data);

    Object visit(ASTObjectProperty node, Object data);

    Object visit(ASTParenthesizedExpression node, Object data);

    Object visit(ASTPropertyGet node, Object data);

    Object visit(ASTRegExpLiteral node, Object data);

    Object visit(ASTReturnStatement node, Object data);

    Object visit(ASTScope node, Object data);

    Object visit(ASTStringLiteral node, Object data);

    Object visit(ASTSwitchCase node, Object data);

    Object visit(ASTSwitchStatement node, Object data);

    Object visit(ASTThrowStatement node, Object data);

    Object visit(ASTTryStatement node, Object data);

    Object visit(ASTUnaryExpression node, Object data);

    Object visit(ASTVariableDeclaration node, Object data);

    Object visit(ASTVariableInitializer node, Object data);

    Object visit(ASTWhileLoop node, Object data);

    Object visit(ASTWithStatement node, Object data);

    Object visit(ASTXmlDotQuery node, Object data);

    Object visit(ASTXmlExpression node, Object data);

    Object visit(ASTXmlMemberGet node, Object data);

    Object visit(ASTXmlString node, Object data);
}
