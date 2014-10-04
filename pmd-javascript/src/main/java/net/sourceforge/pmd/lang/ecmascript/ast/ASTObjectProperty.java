/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ObjectProperty;

public class ASTObjectProperty extends AbstractInfixEcmascriptNode<ObjectProperty> {
    public ASTObjectProperty(ObjectProperty objectProperty) {
	super(objectProperty);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean isGetter() {
	return node.isGetter();
    }

    public boolean isSetter() {
	return node.isSetter();
    }
}
