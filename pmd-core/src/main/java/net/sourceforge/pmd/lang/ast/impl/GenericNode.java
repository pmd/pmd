/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;


import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.NodeStream.DescendantNodeStream;
import net.sourceforge.pmd.lang.ast.internal.StreamImpl;

public interface GenericNode<N extends GenericNode<N>> extends Node {

    @Override
    N getChild(int index);


    @Override
    N getParent();


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
}
