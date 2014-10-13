/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ArrayLiteral;

public class ASTArrayLiteral extends AbstractEcmascriptNode<ArrayLiteral> implements DestructuringNode, TrailingCommaNode {
    private boolean trailingComma;

    public ASTArrayLiteral(ArrayLiteral arrayLiteral) {
	super(arrayLiteral);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean isDestructuring() {
	return node.isDestructuring();
    }

    public boolean isTrailingComma() {
	return trailingComma;
    }

    public void setTrailingComma(boolean trailingComma) {
	this.trailingComma = trailingComma;
    }
}
