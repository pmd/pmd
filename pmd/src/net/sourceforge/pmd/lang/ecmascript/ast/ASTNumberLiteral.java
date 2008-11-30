/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.NumberLiteral;

// TODO The Rhino node does not tell us whether this was specified via decimal, octal or hexidecimal.
public class ASTNumberLiteral extends AbstractEcmascriptNode<NumberLiteral> {
    public ASTNumberLiteral(NumberLiteral numberLiteral) {
	super(numberLiteral);
	super.setImage(numberLiteral.getValue());
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
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
