/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.XmlLiteral;

public final class ASTXmlLiteral extends AbstractEcmascriptNode<XmlLiteral> {

    ASTXmlLiteral(XmlLiteral xmlLiteral) {
        super(xmlLiteral);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
