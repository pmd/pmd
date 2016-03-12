/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.ConditionalExpression;

public class ASTConditionalExpression extends AbstractApexNode<ConditionalExpression> {
    public ASTConditionalExpression(ConditionalExpression conditionalExpression) {
	super(conditionalExpression);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ApexNode<?> getTestExpression() {
	return (ApexNode<?>) jjtGetChild(0);
    }

    public ApexNode<?> getTrueExpression() {
	return (ApexNode<?>) jjtGetChild(1);
    }

    public ApexNode<?> getFalseExpression() {
	return (ApexNode<?>) jjtGetChild(2);
    }
}
