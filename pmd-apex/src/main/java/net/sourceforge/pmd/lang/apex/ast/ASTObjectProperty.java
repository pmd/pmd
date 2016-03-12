/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.ObjectProperty;

public class ASTObjectProperty extends AbstractInfixApexNode<ObjectProperty> {
    public ASTObjectProperty(ObjectProperty objectProperty) {
	super(objectProperty);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean isGetter() {
	return node.isGetterMethod();
    }

    public boolean isSetter() {
	return node.isSetterMethod();
    }
}
