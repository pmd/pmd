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
        if (jjtGetNumChildren() > 0) {
            return (EcmascriptNode)jjtGetChild(0);
        }
        return null;
    }

    public EcmascriptNode getElement() {
        if (jjtGetNumChildren() > 1) {
            return (EcmascriptNode)jjtGetChild(1);
        }
        return null;
    }
}
