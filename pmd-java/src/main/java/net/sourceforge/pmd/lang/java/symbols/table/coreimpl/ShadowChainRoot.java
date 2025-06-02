/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;

import static java.util.Collections.emptyList;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.util.OptionalBool;


/**
 * An empty group, bottom of the linked lists, for implementation simplicity.
 */
final class ShadowChainRoot<S, I> implements ShadowChain<S, I>, ShadowChainNode<S, I> {

    @SuppressWarnings("rawtypes")
    private static final ShadowChainRoot EMPTY = new ShadowChainRoot<>();

    private ShadowChainRoot() {
    }

    @Override
    public ShadowChain<S, I> asChain() {
        return this;
    }

    @Override
    public ShadowChainNode<S, I> asNode() {
        return this;
    }

    @Override
    public NameResolver<S> getResolver() {
        return CoreResolvers.emptyResolver();
    }

    @Override
    public OptionalBool knowsSymbol(String name) {
        return OptionalBool.NO;
    }

    @Override
    public @Nullable ShadowChainNode<S, I> getParent() {
        return null;
    }

    @Override
    public boolean isShadowBarrier() {
        return true;
    }

    @Override
    public @NonNull List<S> resolve(String name) {
        return emptyList();
    }

    @Override
    public S resolveFirst(String name) {
        return null;
    }

    @Override
    public String toString() {
        return "Root";
    }

    @SuppressWarnings("unchecked")
    static <S, I> ShadowChainNode<S, I> empty() {
        return EMPTY;
    }
}
