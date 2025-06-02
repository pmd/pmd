/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;


import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.util.IteratorUtil;
import net.sourceforge.pmd.util.OptionalBool;

class ShadowChainIteratorImpl<S, I>
    extends IteratorUtil.AbstractPausingIterator<ShadowChainNode<S, I>>
    implements ShadowChainIterator<S, I> {

    private ShadowChainNode<S, I> nextGroupToTest;
    private final String name;

    ShadowChainIteratorImpl(ShadowChainNode<S, I> firstInclusive, String name) {
        this.nextGroupToTest = firstInclusive;
        this.name = name;
    }

    // FIXME cross shadow barrier

    @Override
    protected void computeNext() {
        ShadowChainNode<S, I> next = nextGroupThatKnows(nextGroupToTest, name);
        if (next == null) {
            done();
            return;
        }
        assert !next.resolveHere(name).isEmpty() : "Shadow iterator stopped on wrong node";
        setNext(next);
    }


    @Override
    protected void prepareViewOn(ShadowChainNode<S, I> current) {
        if (current instanceof ShadowChainNodeBase) {
            nextGroupToTest = current.getParent();
        } else {
            throw new IllegalStateException("Root group is empty " + current);
        }
    }

    @Override
    public I getScopeTag() {
        return ((ShadowChainNodeBase<S, I>) getCurrentValue()).getScopeTag();
    }

    @Override
    public List<S> getResults() {
        return getCurrentValue().resolveHere(name);
    }

    // inclusive of the parameter
    // @Contract("null -> null")
    private @Nullable ShadowChainNode<S, I> nextGroupThatKnows(@Nullable ShadowChainNode<S, I> group, String name) {
        ShadowChainNode<S, I> parent = group;
        while (parent != null && !definitelyKnows(parent, name)) {
            parent = parent.getParent();
        }
        return parent;
    }

    private static boolean definitelyKnows(@NonNull ShadowChainNode<?, ?> group, String name) {
        // It's not cool to depend on the implementation, but doing otherwise is publishing a lot of API
        OptionalBool opt = group.knowsSymbol(name);
        if (opt.isKnown()) {
            return opt.isTrue();
        }
        // Note that we bypass the node to call directly the resolver
        // This is to bypass the cache of cached resolvers, which stores declarations
        // of enclosing groups
        return group.getResolver().resolveFirst(name) != null;
    }

}
