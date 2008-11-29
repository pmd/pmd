/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ElementGet;

public class ASTElementGet extends AbstractEcmascriptNode<ElementGet> {
    public ASTElementGet(ElementGet elementGet) {
	super(elementGet);
    }

    public EcmascriptNode getTarget() {
	return (EcmascriptNode) node.getTarget();
    }

    public EcmascriptNode getElement() {
	return (EcmascriptNode) node.getElement();
    }
}
