/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.XmlMemberGet;

public class ASTXmlMemberGet extends AbstractInfixApexNode<XmlMemberGet> {
    public ASTXmlMemberGet(XmlMemberGet xmlMemberGet) {
	super(xmlMemberGet);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
