/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.InfixExpression;

public class ASTInfixExpression extends AbstractInfixApexNode<InfixExpression> {
    public ASTInfixExpression(InfixExpression infixExpression) {
	super(infixExpression);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
