/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.PropertyGet;

public class ASTPropertyGet extends AbstractInfixApexNode<PropertyGet> {
    public ASTPropertyGet(PropertyGet propertyGet) {
	super(propertyGet, false);
	super.setImage(".");
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
