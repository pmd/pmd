/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

public class ApexParserVisitorAdapter implements ApexParserVisitor {
	@Override
	public Object visit(ApexNode<?> node, Object data) {
		node.childrenAccept(this, data);
		return null;
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
		// TODO Auto-generated method stub
		return null;
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
	public Object visit(ASTCompilation node, Object data) {
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
}
