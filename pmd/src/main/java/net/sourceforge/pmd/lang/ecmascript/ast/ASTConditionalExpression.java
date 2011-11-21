/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ConditionalExpression;

public class ASTConditionalExpression extends AbstractEcmascriptNode<ConditionalExpression> {
    public ASTConditionalExpression(ConditionalExpression conditionalExpression) {
	super(conditionalExpression);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public EcmascriptNode getTestExpression() {
	return (EcmascriptNode) jjtGetChild(0);
    }

    public EcmascriptNode getTrueExpression() {
	return (EcmascriptNode) jjtGetChild(1);
    }

    public EcmascriptNode getFalseExpression() {
	return (EcmascriptNode) jjtGetChild(2);
    }
}
