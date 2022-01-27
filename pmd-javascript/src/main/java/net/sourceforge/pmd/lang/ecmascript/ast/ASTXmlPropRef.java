/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.XmlPropRef;

public final class ASTXmlPropRef extends AbstractEcmascriptNode<XmlPropRef> {

    ASTXmlPropRef(XmlPropRef xmlPropRef) {
        super(xmlPropRef);

        Name propName = xmlPropRef.getPropName();
        if (propName != null) {
            super.setImage(propName.getIdentifier());
        }
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
