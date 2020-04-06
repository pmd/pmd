/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;


import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.util.OptionalBool;

class ShadowChainIteratorImpl<S, I>
    extends IteratorUtil.AbstractPausingIterator<ShadowChain<S, I>>
    implements ShadowChainIterator<S, I> {

    private ShadowChain<S, I> nextGroupToTest;
    private final String name;

    ShadowChainIteratorImpl(ShadowChain<S, I> firstInclusive, String name) {
        this.nextGroupToTest = firstInclusive;
        this.name = name;
    }

    // FIXME cross shadow barrier

    @Override
    protected void computeNext() {
        ShadowChain<S, I> next = nextGroupThatKnows(nextGroupToTest, name);
        if (next == null) {
            done();
            return;
        }
        setNext(next);
    }


    @Override
    protected void prepareViewOn(ShadowChain<S, I> current) {
        if (current instanceof ShadowChainNode) {
            nextGroupToTest = current.getParent();
        } else {
            throw new IllegalStateException("Root group is empty " + current);
        }
    }

    @Override
    public I getScopeTag() {
        return ((ShadowChainNode<S, I>) getCurrentValue()).getScopeTag();
    }

    @Override
    public List<S> getResults() {
        return getCurrentValue().resolve(name);
    }

    // inclusive of the parameter
    // @Contract("null -> null")
    private @Nullable ShadowChain<S, I> nextGroupThatKnows(@Nullable ShadowChain<S, I> group, String name) {
        ShadowChain<S, I> parent = group;
        while (parent != null && !definitelyKnows(parent, name)) {
            parent = parent.getParent();
        }
        return parent;
    }

    private static boolean definitelyKnows(@NonNull ShadowChain<?, ?> group, String name) {
        if (group instanceof ShadowChainNode) {
            OptionalBool opt = ((ShadowChainNode<?, ?>) group).knowsSymbol(name);
            return opt.isKnown()
                   ? opt.isTrue()
                   // Note that we bypass the shadow group to call directly the resolver
                   // This is to bypass the cache of cached resolvers, which stores declarations
                   // of enclosing groups
                   : ((ShadowChainNode<?, ?>) group).resolver.resolveFirst(name) != null;
        } else {
            assert group instanceof ShadowChainRoot : "Not a root shadow group? " + group;
            return false;
        }
    }

}
