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
}
