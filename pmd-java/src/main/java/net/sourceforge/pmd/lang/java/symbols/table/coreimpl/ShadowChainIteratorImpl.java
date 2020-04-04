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
    extends IteratorUtil.AbstractPausingIterator<ShadowGroup<S, I>>
    implements ShadowChainIterator<S, I> {

    private ShadowGroup<S, I> nextGroupToTest;
    private final String name;

    ShadowChainIteratorImpl(ShadowGroup<S, I> firstInclusive, String name) {
        this.nextGroupToTest = firstInclusive;
        this.name = name;
    }

    // FIXME cross shadow barrier

    @Override
    protected void computeNext() {
        ShadowGroup<S, I> next = nextGroupThatKnows(nextGroupToTest, name);
        if (next == null) {
            done();
            return;
        }
        setNext(next);
    }


    @Override
    protected void prepareViewOn(ShadowGroup<S, I> current) {
        if (current instanceof SimpleShadowGroup) {
            nextGroupToTest = current.getParent();
        } else {
            throw new IllegalStateException("Root group is empty " + current);
        }
    }

    @Override
    public I getScopeTag() {
        return ((SimpleShadowGroup<S, I>) getCurrentValue()).getScopeTag();
    }

    @Override
    public List<S> getResults() {
        return getCurrentValue().resolve(name);
    }

    // inclusive of the parameter
    // @Contract("null -> null")
    private @Nullable ShadowGroup<S, I> nextGroupThatKnows(@Nullable ShadowGroup<S, I> group, String name) {
        ShadowGroup<S, I> parent = group;
        while (parent != null && !definitelyKnows(parent, name)) {
            parent = parent.getParent();
        }
        return parent;
    }

    private static boolean definitelyKnows(@NonNull ShadowGroup<?, ?> group, String name) {
        if (group instanceof SimpleShadowGroup) {
            OptionalBool opt = ((SimpleShadowGroup<?, ?>) group).knowsSymbol(name);
            return opt.isKnown()
                   ? opt.isTrue()
                   // Note that we bypass the shadow group to call directly the resolver
                   // This is to bypass the cache of cached resolvers, which stores declarations
                   // of enclosing groups
                   : ((SimpleShadowGroup<?, ?>) group).resolver.resolveFirst(name) != null;
        } else {
            assert group instanceof RootShadowGroup : "Not a root shadow group? " + group;
            return false;
        }
    }

}
