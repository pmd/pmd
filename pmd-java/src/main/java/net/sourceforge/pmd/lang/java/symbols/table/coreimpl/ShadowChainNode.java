/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.util.OptionalBool;

/**
 * A {@link ShadowChain} viewed as individual nodes. This offers a lower
 * level API as {@link ShadowChain}.
 */
public interface ShadowChainNode<S, I> {


    /**
     * Returns true if this group shadows the next groups in the chain.
     * This means, that if this group knows about a name, it won't delegate
     * resolve to the next group in the chain. If it doesn't know about it
     * then resolve proceeds anyway.
     */
    boolean isShadowBarrier();


    /**
     * Returns the next node in the chain. Returns null if this is the
     * root.
     */
    @Nullable ShadowChainNode<S, I> getParent();


    /**
     * Returns the resolver for this node.
     */
    NameResolver<S> getResolver();


    /**
     * Returns whether this node knows the given symbol (without asking
     * the parents).
     */
    OptionalBool knowsSymbol(String name);


    ShadowChain<S, I> asChain();

}
