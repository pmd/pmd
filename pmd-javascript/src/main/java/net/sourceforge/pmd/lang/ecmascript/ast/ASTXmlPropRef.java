/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.XmlPropRef;

public final class ASTXmlPropRef extends AbstractEcmascriptNode<XmlPropRef> {

    ASTXmlPropRef(XmlPropRef xmlPropRef) {
        super(xmlPropRef);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public ASTName getPropName() {
        // first ASTName would be namespace
        return children(ASTName.class).last();
    }
}
