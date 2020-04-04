/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;

import static java.util.Collections.emptyList;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * An empty group, bottom of the linked lists, for implementation simplicity.
 */
class RootShadowGroup<S, I> implements ShadowGroup<S, I> {

    @SuppressWarnings( {"rawtypes"})
    private static final RootShadowGroup EMPTY = new RootShadowGroup<>();

    private RootShadowGroup() {
    }

    @Override
    public @Nullable ShadowGroup<S, I> getParent() {
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

    static <S, I> ShadowGroup<S, I> empty() {
        return EMPTY;
    }
}
