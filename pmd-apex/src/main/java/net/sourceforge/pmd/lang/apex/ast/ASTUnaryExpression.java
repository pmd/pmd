/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.Token;
import org.mozilla.apex.ast.AstRoot;
import org.mozilla.apex.ast.UnaryExpression;

public class ASTUnaryExpression extends AbstractApexNode<UnaryExpression> {
    public ASTUnaryExpression(UnaryExpression unaryExpression) {
	super(unaryExpression);
	if (unaryExpression.getOperator() == Token.VOID) {
	    super.setImage("void");
	} else {
	    super.setImage(AstRoot.operatorToString(unaryExpression.getOperator()));
	}
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
    
    public ApexNode<?> getOperand() {
	return (ApexNode<?>)jjtGetChild(0);
    }

    public boolean isPrefix() {
	return node.isPrefix();
    }

    public boolean isPostfix() {
	return node.isPostfix();
    }
}
