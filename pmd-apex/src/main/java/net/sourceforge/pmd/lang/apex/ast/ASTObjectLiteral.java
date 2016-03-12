/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.ObjectLiteral;

public class ASTObjectLiteral extends AbstractApexNode<ObjectLiteral> implements DestructuringNode, TrailingCommaNode {
    private boolean trailingComma;

    public ASTObjectLiteral(ObjectLiteral objectLiteral) {
	super(objectLiteral);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ASTObjectProperty getObjectProperty(int index) {
	return (ASTObjectProperty) jjtGetChild(index);
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
