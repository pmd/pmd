/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.XmlString;

public final class ASTXmlString extends AbstractEcmascriptNode<XmlString> {
    ASTXmlString(XmlString xmlString) {
        super(xmlString);
        super.setImage(xmlString.getXml());
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
