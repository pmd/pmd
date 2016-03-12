/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.Token;
import org.mozilla.apex.ast.AstRoot;
import org.mozilla.apex.ast.InfixExpression;

public class AbstractInfixApexNode<T extends InfixExpression> extends AbstractApexNode<T> {

    public AbstractInfixApexNode(T infixExpression) {
	this(infixExpression, true);
    }

    public AbstractInfixApexNode(T infixExpression, boolean setImage) {
	super(infixExpression);
	if (setImage) {
	    if (infixExpression.getOperator() == Token.ASSIGN_BITXOR) {
	        super.setImage("^=");
	    } else {
	        super.setImage(AstRoot.operatorToString(infixExpression.getOperator()));
	    }
	}
    }

    public ApexNode<?> getLeft() {
	return (ApexNode<?>) jjtGetChild(0);
    }

    public ApexNode<?> getRight() {
	return (ApexNode<?>) jjtGetChild(1);
    }
}
