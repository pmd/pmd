/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.StringLiteral;

public class ASTStringLiteral extends AbstractApexNode<StringLiteral> {
    public ASTStringLiteral(StringLiteral stringLiteral) {
	super(stringLiteral);
	super.setImage(stringLiteral.getValue());
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public char getQuoteCharacter() {
	return node.getQuoteCharacter();
    }

    public boolean isSingleQuoted() {
	return '\'' == getQuoteCharacter();
    }

    public boolean isDoubleQuoted() {
	return '"' == getQuoteCharacter();
    }
}
