/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;


import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.NodeStream.DescendantNodeStream;
import net.sourceforge.pmd.lang.ast.internal.StreamImpl;

/**
 * Interface that binds the return type of some node methods to a type
 * parameter. This enforces that eg all children of such a node are from
 * the same hierarchy (eg Java nodes only have Java nodes as parent, or
 * as children).
 *
 * <p>Although subinterfaces like JavaNode profit from the added type
 * information, the Node interface and its usages in language-independent
 * code would suffer from adding a type parameter directly to {@link Node}.
 *
 * <p>Type safety of the unchecked casts here is the responsibility of
 * the implementation, it should check that methods like setParent or
 * addChild add an instance of {@code <N>}.
 *
 * @param <N> Self type (eg JavaNode)
 */
@SuppressWarnings("unchecked")
public interface GenericNode<N extends GenericNode<N>> extends Node {

    @Override
    N getChild(int index);

    @Override
    N getParent();

    @Override
    @Nullable
    default N getFirstChild() {
        return getNumChildren() > 0 ? getChild(0) : null;
    }

    @Override
    @Nullable
    default N getLastChild() {
        return getNumChildren() > 0 ? getChild(getNumChildren() - 1) : null;
    }

    @Override
    default NodeStream<N> asStream() {
        return StreamImpl.singleton((N) this);
    }

    @Override
    default N getNthParent(int n) {
        return (N) Node.super.getNthParent(n);
    }

    @Override
    default NodeStream<? extends N> children() {
        return (NodeStream<? extends N>) Node.super.children();
    }

    @Override
    default DescendantNodeStream<N> descendants() {
        return (DescendantNodeStream<N>) Node.super.descendants();
    }

    @Override
    default DescendantNodeStream<N> descendantsOrSelf() {
        return (DescendantNodeStream<N>) Node.super.descendantsOrSelf();
    }

    @Override
    default NodeStream<N> ancestorsOrSelf() {
        return (NodeStream<N>) Node.super.ancestorsOrSelf();
    }

    @Override
    default NodeStream<N> ancestors() {
        return (NodeStream<N>) Node.super.ancestors();
    }

    @Override
    default @Nullable N getPreviousSibling() {
        return (N) Node.super.getPreviousSibling();
    }

    @Override
    default @Nullable N getNextSibling() {
        return (N) Node.super.getNextSibling();
    }
}
