/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.ElementGet;

public class ASTElementGet extends AbstractApexNode<ElementGet> {
    public ASTElementGet(ElementGet elementGet) {
	super(elementGet);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ApexNode<?> getTarget() {
        if (jjtGetNumChildren() > 0) {
            return (ApexNode<?>)jjtGetChild(0);
        }
        return null;
    }

    public ApexNode<?> getElement() {
        if (jjtGetNumChildren() > 1) {
            return (ApexNode<?>)jjtGetChild(1);
        }
        return null;
    }
}
