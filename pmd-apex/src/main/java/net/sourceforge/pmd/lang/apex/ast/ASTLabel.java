/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.Label;

public class ASTLabel extends AbstractApexNode<Label> {
    public ASTLabel(Label label) {
	super(label);
	super.setImage(label.getName());
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}