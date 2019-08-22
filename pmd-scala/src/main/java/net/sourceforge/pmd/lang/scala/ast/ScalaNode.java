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
     * @param <D>
     *            The type of the data input
     * @param <R>
     *            The type of the returned data
     * @param visitor
     *            the visitor to visit this node with
     * @param data
     *            context-specific data to pass along
     * @return context-specific data for this Visitor pattern
     */
    <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data);

    /**
     * Accept the visitor against all children of this node if there are any and
     * return.
     * 
     * @param <D>
     *            The type of the data input
     * @param <R>
     *            The type of the returned data
     * @param visitor
     *            the visitor to visit this node's children with
     * @param data
     *            context-specific data to pass along
     * @return context-specific data for this Visitor pattern
     */
    <D, R> R childrenAccept(ScalaParserVisitor<D, R> visitor, D data);

    /**
     * Get the underlying Scala Node.
     * 
     * @return the Scala Node for this node
     */
    T getNode();
}
