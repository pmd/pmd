/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.XmlExpression;

public class ASTXmlExpression extends AbstractApexNode<XmlExpression> {
    public ASTXmlExpression(XmlExpression xmlExpression) {
	super(xmlExpression);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ApexNode<?> getExpression() {
	return (ApexNode<?>) jjtGetChild(0);
    }

    public boolean isXmlAttribute() {
	return node.isXmlAttribute();
    }
}
