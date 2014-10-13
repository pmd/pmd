/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.InfixExpression;

public class ASTInfixExpression extends AbstractInfixEcmascriptNode<InfixExpression> {
    public ASTInfixExpression(InfixExpression infixExpression) {
	super(infixExpression);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
