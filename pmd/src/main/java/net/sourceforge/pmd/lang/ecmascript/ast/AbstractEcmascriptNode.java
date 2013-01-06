/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import net.sourceforge.pmd.lang.ast.AbstractNode;

import org.mozilla.javascript.ast.AstNode;

public abstract class AbstractEcmascriptNode<T extends AstNode> extends AbstractNode implements EcmascriptNode<T> {

    protected final T node;

    public AbstractEcmascriptNode(T node) {
	super(node.getType());
	this.node = node;
    }

    /* package private */
    void calculateLineNumbers(SourceCodePositioner positioner) {
	int startOffset = node.getAbsolutePosition();
	int endOffset = startOffset + node.getLength();

	this.beginLine = positioner.lineNumberFromOffset(startOffset);
	this.beginColumn = positioner.columnFromOffset(startOffset);
	this.endLine = positioner.lineNumberFromOffset(endOffset);
	this.endColumn = positioner.columnFromOffset(endOffset) - 1; // end column is inclusive
	if (this.endColumn < 0) {
	    this.endColumn = 0;
	}
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
		@SuppressWarnings("unchecked") // we know that the children here are all EcmascriptNodes
		EcmascriptNode<T> ecmascriptNode = (EcmascriptNode<T>) children[i];
		ecmascriptNode.jjtAccept(visitor, data);
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
    public String toString() {
	return node.shortName();
    }
}
