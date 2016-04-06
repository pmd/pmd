package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.ArrayStoreExpression;

public class ASTArrayStoreExpression extends AbstractApexNode<ArrayStoreExpression> {

	public ASTArrayStoreExpression(ArrayStoreExpression arrayStoreExpression) {
		super(arrayStoreExpression);
	}

	public Object jjtAccept(ApexParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}