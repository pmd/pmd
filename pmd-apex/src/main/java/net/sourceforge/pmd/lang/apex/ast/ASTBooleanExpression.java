package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.BooleanExpression;

public class ASTBooleanExpression extends AbstractApexNode<BooleanExpression> {

	public ASTBooleanExpression(BooleanExpression booleanExpression) {
		super(booleanExpression);
	}

    /**
     * Accept the visitor.
     * Note: This needs to be in each concrete node class, as otherwise
     * the visitor won't work - as java resolves the type "this" at compile
     * time.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}