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
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
