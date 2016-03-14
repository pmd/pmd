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
		// TODO Auto-generated method stub
		return null;
	}
}
