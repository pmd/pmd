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

	Object visit(ASTConstructorPreambleStatement node, Object data);

	Object visit(ASTUserInterface node, Object data);

	Object visit(ASTUserEnum node, Object data);

	Object visit(ASTFieldDeclaration node, Object data);

	Object visit(ASTWhileLoopStatement node, Object data);

	Object visit(ASTTryCatchFinallyBlockStatement node, Object data);

	Object visit(ASTForLoopStatement node, Object data);

	Object visit(ASTIfElseBlockStatement node, Object data);

	Object visit(ASTIfBlockStatement node, Object data);

	Object visit(ASTForEachStatement node, Object data);
	
	Object visit(ASTDoLoopStatement node, Object data);
	
	Object visit(ASTTernaryExpression node, Object data);

	Object visit(ASTCompilation node, Object data);

	Object visit(ASTField node, Object data);

	Object visit(ASTBreakStatement node, Object data);

	Object visit(ASTThrowStatement node, Object data);
}
