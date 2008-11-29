/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ArrayLiteral;

public class ASTArrayLiteral extends AbstractEcmascriptNode<ArrayLiteral> implements DestructuringNode {
    public ASTArrayLiteral(ArrayLiteral arrayLiteral) {
	super(arrayLiteral);
    }

    public boolean isDestructuring() {
	return node.isDestructuring();
    }
}
