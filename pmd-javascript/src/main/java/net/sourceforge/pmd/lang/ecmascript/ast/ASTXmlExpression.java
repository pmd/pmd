/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.XmlExpression;

public class ASTXmlExpression extends AbstractEcmascriptNode<XmlExpression> {
    ASTXmlExpression(XmlExpression xmlExpression) {
        super(xmlExpression);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getExpression() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public boolean isXmlAttribute() {
        return node.isXmlAttribute();
    }
}
