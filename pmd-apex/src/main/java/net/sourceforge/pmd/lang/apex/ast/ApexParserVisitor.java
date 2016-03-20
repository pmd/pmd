/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

public interface ApexParserVisitor {
    Object visit(ApexNode<?> node, Object data);
    Object visit(ASTUserClass node, Object data);
    Object visit(ASTMethod node, Object data);
    Object visit(ASTModifierNode node, Object data);
    Object visit(ASTParameter node, Object data);
    Object visit(ASTBlockStatement node, Object data);
    Object visit(ASTUserClassMethods node, Object data);
    Object visit(ASTBridgeMethodCreator node, Object data);
    Object visit(ASTReturnStatement node, Object data);
    Object visit(ASTLiteralExpression node, Object data);
}
