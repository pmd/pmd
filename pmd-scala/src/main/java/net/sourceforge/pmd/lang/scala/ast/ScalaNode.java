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
     * Get the underlying Scala Node.
     *
     * @return the Scala Node for this node
     */
    T getNode();


    /**
     * Returns true if the node is implicit. If this node has no non-implicit
     * descendant, then its text bounds identify an empty region of the source
     * document. In that case, the {@linkplain #getEndColumn() end column} is
     * smaller than the {@linkplain #getBeginColumn() begin column}. That's
     * because the end column index is inclusive.
     */
    // TODO this would be useful on the node interface for 7.0.0.
    //  we could filter them out from violations transparently
    //  Apex has the same problem
    boolean isImplicit();


    @Override
    ScalaNode<?> getChild(int idx);


    @Override
    ScalaNode<?> getParent();


    @Override
    Iterable<? extends ScalaNode<?>> children();
}
