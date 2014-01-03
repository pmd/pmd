/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.XmlMemberGet;

public class ASTXmlMemberGet extends AbstractInfixEcmascriptNode<XmlMemberGet> {
    public ASTXmlMemberGet(XmlMemberGet xmlMemberGet) {
	super(xmlMemberGet);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
