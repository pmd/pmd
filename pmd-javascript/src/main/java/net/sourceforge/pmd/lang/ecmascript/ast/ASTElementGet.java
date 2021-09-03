/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ElementGet;

public final class ASTElementGet extends AbstractEcmascriptNode<ElementGet> {
    ASTElementGet(ElementGet elementGet) {
        super(elementGet);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getTarget() {
        if (getNumChildren() > 0) {
            return (EcmascriptNode<?>) getChild(0);
        }
        return null;
    }

    public EcmascriptNode<?> getElement() {
        if (getNumChildren() > 1) {
            return (EcmascriptNode<?>) getChild(1);
        }
        return null;
    }
}
