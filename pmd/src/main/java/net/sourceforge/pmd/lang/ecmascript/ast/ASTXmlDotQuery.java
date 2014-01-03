/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.XmlDotQuery;

public class ASTXmlDotQuery extends AbstractInfixEcmascriptNode<XmlDotQuery> {
    public ASTXmlDotQuery(XmlDotQuery xmlDotQuery) {
	super(xmlDotQuery);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
