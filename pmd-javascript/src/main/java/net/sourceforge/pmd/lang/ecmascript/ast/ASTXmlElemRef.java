/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.XmlElemRef;

public final class ASTXmlElemRef extends AbstractEcmascriptNode<XmlElemRef> {

    ASTXmlElemRef(XmlElemRef xmlElemRef) {
        super(xmlElemRef);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
