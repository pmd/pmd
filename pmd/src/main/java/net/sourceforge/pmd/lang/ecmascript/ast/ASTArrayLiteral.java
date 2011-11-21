/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ArrayLiteral;

public class ASTArrayLiteral extends AbstractEcmascriptNode<ArrayLiteral> implements DestructuringNode {
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
}
