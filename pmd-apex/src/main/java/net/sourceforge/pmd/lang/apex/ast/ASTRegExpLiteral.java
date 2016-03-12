/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.RegExpLiteral;

public class ASTRegExpLiteral extends AbstractApexNode<RegExpLiteral> {
    public ASTRegExpLiteral(RegExpLiteral regExpLiteral) {
	super(regExpLiteral);
	super.setImage(regExpLiteral.getValue());
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public String getFlags() {
	return node.getFlags();
    }
}