package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.condition.StandardCondition;

public class ASTStandardCondition extends AbstractApexNode<StandardCondition> {

	public ASTStandardCondition(StandardCondition standardCondition) {
		super(standardCondition);
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