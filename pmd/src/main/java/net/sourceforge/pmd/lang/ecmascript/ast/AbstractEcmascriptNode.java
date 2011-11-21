/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import net.sourceforge.pmd.lang.ast.AbstractNode;

import org.mozilla.javascript.ast.AstNode;

public abstract class AbstractEcmascriptNode<T extends AstNode> extends AbstractNode implements EcmascriptNode {

    protected final T node;

    public AbstractEcmascriptNode(T node) {
	super(node.getType());
	this.node = node;
	this.beginLine = node.getLineno() + 1;
	this.beginLine = node.getLineno() + 1;
	// TODO Implement positions, or figure out how to do begin/end lines/column
	//this.beginPosition = node.getAbsolutePosition();
	//this.endPosition = this.beginPosition + node.getLength();
    }

    /**
     * Accept the visitor. *
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    /**
     * Accept the visitor. *
     */
    public Object childrenAccept(EcmascriptParserVisitor visitor, Object data) {
	if (children != null) {
	    for (int i = 0; i < children.length; ++i) {
		((EcmascriptNode) children[i]).jjtAccept(visitor, data);
	    }
	}
	return data;
    }
    
    public T getNode() {
	return node;
    }

    public String getJsDoc() {
	return node.getJsDoc();
    }

    public boolean hasSideEffects() {
	return node.hasSideEffects();
    }

    @Override
    public int getBeginColumn() {
	return -1;
    }

    @Override
    public String toString() {
	return node.shortName();
    }
}
