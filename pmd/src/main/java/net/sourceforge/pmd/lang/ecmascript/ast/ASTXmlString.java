package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.XmlString;

public class ASTXmlString extends AbstractEcmascriptNode<XmlString> {
    public ASTXmlString(XmlString xmlString) {
	super(xmlString);
	super.setImage(xmlString.getXml());
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
