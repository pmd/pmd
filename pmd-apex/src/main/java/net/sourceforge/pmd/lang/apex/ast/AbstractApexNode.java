/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.AbstractNode;

import apex.jorje.semantic.ast.AstNode; 

public abstract class AbstractApexNode<T extends AstNode> extends AbstractNode implements ApexNode<T> {

	protected final T node;

	public AbstractApexNode(T node) {
		super(node.getDefiningType().hashCode());
		this.node = node;
	}

	/**
	 * Accept the visitor. *
	 */
	public Object jjtAccept(ApexParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}

	/**
	 * Accept the visitor. *
	 */
	public Object childrenAccept(ApexParserVisitor visitor, Object data) {
		if (children != null) {
			for (int i = 0; i < children.length; ++i) {
				@SuppressWarnings("unchecked")
				// we know that the children here are all ApexNodes
				ApexNode<T> apexNode = (ApexNode<T>) children[i];
				apexNode.jjtAccept(visitor, data);
			}
		}
		return data;
	}

	public T getNode() {
		return node;
	}
}
