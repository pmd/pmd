/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.Node;

import apex.jorje.semantic.ast.AstNode;

/**
 * Root interface implemented by all Apex nodes. Apex nodes wrap a tree
 * obtained from an external parser (Jorje). The underlying AST node is
 * available with {@link #getNode()}.
 *
 * @param <T> Type of the underlying Jorje node
 */
public interface ApexNode<T extends AstNode> extends Node {

    /**
     * Accept the visitor.
     */
    Object jjtAccept(ApexParserVisitor visitor, Object data);


    /**
     * Accept the visitor. *
     *
     * @deprecated This method is not useful, the logic for combining
     *     children values should be present on the visitor, not the node
     */
    @Deprecated
    Object childrenAccept(ApexParserVisitor visitor, Object data);


    /**
     * Get the underlying AST node.
     */
    T getNode();


    @Override
    Iterable<? extends ApexNode<?>> children();


    @Override
    ApexNode<?> getChild(int index);


    @Override
    ApexNode<?> getParent();
}
