/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.XmlString;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTXmlString extends AbstractEcmascriptNode<XmlString> {
    @Deprecated
    @InternalApi
    public ASTXmlString(XmlString xmlString) {
        super(xmlString);
        super.setImage(xmlString.getXml());
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
