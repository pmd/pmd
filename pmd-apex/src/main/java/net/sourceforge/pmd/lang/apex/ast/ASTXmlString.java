/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.XmlString;

public class ASTXmlString extends AbstractApexNode<XmlString> {
    public ASTXmlString(XmlString xmlString) {
	super(xmlString);
	super.setImage(xmlString.getXml());
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
