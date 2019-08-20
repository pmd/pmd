/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.lang.ast.Node;

import scala.meta.Tree;

/**
 * A Base interface of a Scala Node. Defines several required methods of all
 * nodes.
 * 
 * @param <T>
 *            The Scala node type that extends Scala's Tree trait
 */
public interface ScalaNode<T extends Tree> extends Node {
    /**
     * Accept a visitor and traverse this node.
     * 
     * @param visitor
     *            the visitor to visit this node with
     * @param data
     *            context-specific data to pass along
     * @return context-specific data for this Visitor pattern
     */
    Object accept(ScalaParserVisitor visitor, Object data);

    /**
     * Accept the visitor against all children of this node if there are any and
     * return.
     * 
     * @param visitor
     *            the visitor to visit this node's children with
     * @param data
     *            context-specific data to pass along
     * @return context-specific data for this Visitor pattern
     */
    Object childrenAccept(ScalaParserVisitor visitor, Object data);

    /**
     * Get the underlying Scala Node.
     * 
     * @return the Scala Node for this node
     */
    T getNode();
}
