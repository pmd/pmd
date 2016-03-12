/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.XmlDotQuery;

public class ASTXmlDotQuery extends AbstractInfixApexNode<XmlDotQuery> {
    public ASTXmlDotQuery(XmlDotQuery xmlDotQuery) {
	super(xmlDotQuery);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
