/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;

import scala.meta.Tree;

/**
 * A Base interface of a Scala Node. Defines several required methods of all
 * nodes.
 *
 * @param <T>
 *            The Scala node type that extends Scala's Tree trait
 */
public interface ScalaNode<T extends Tree> extends GenericNode<ScalaNode<?>> {

    /**
     * Accept a visitor and traverse this node.
     *
     * @deprecated Use {@link #acceptVisitor(AstVisitor, Object)}
     */
    @Deprecated
    default <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return acceptVisitor(visitor, data);
    }


    /**
     * Get the underlying Scala Node.
     *
     * @return the Scala Node for this node
     * @deprecated The underlying scala node should not be used directly.
     */
    @Deprecated
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
}
