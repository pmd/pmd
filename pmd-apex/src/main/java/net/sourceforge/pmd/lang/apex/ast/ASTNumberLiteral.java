/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.NumberLiteral;

// TODO The Rhino node does not tell us whether this was specified via decimal, octal or hexidecimal.
public class ASTNumberLiteral extends AbstractApexNode<NumberLiteral> {
    public ASTNumberLiteral(NumberLiteral numberLiteral) {
	super(numberLiteral);
	super.setImage(numberLiteral.getValue());
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public String getNormalizedImage() {
	// TODO Implement
	return super.getImage();
    }

    public double getNumber() {
	return node.getNumber();
    }
}
