/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ElementGet;

public class ASTElementGet extends AbstractEcmascriptNode<ElementGet> {
    public ASTElementGet(ElementGet elementGet) {
	super(elementGet);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public EcmascriptNode getTarget() {
        return EcmascriptTreeBuilder.createNodeAdapter(node.getTarget());
    }

    public EcmascriptNode getElement() {
        return EcmascriptTreeBuilder.createNodeAdapter(node.getElement());
    }
}
