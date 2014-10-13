/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dcd.graph;

/**
 * Interface for how a node should accept a NodeVisitor. The node may choose
 * to send the visitor to it's contained nodes.
 */
public interface NodeVisitorAcceptor {
	Object accept(NodeVisitor visitor, Object data);
}
