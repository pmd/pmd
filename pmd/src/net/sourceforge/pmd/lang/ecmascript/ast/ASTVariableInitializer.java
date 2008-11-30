/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.VariableInitializer;

public class ASTVariableInitializer extends AbstractEcmascriptNode<VariableInitializer> implements DestructuringNode {
    public ASTVariableInitializer(VariableInitializer variableInitializer) {
	super(variableInitializer);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public EcmascriptNode getTarget() {
	return (EcmascriptNode) jjtGetChild(0);
    }

    public EcmascriptNode getInitializer() {
	if (jjtGetNumChildren() > 0) {
	    return (EcmascriptNode) jjtGetChild(1);
	} else {
	    return null;
	}
    }

    public boolean isDestructuring() {
	return node.isDestructuring();
    }
}
