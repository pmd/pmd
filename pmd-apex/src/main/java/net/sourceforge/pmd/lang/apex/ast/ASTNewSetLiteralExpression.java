package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.NewSetLiteralExpression;

public class ASTNewSetLiteralExpression extends AbstractApexNode<NewSetLiteralExpression> {

	public ASTNewSetLiteralExpression(NewSetLiteralExpression newSetLiteralExpression) {
		super(newSetLiteralExpression);
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
