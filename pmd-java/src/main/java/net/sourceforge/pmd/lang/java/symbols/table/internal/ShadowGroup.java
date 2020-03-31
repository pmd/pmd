/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;

/**
 * A shadow group indexes symbols by their simple name.
 */
public final class ShadowGroup<S extends JElementSymbol> {

    private final PMultimap<String, S> symbolsByName;
    private final PMap<String, ShadowGroup<S>> nextShadowGroups;

    private ShadowGroup(PMultimap<String, S> symbolsByName, PMap<String, ShadowGroup<S>> nextShadowGroups) {
        this.symbolsByName = symbolsByName;
        this.nextShadowGroups = nextShadowGroups;
    }

    public @NonNull List<S> resolve(String name) {
        return symbolsByName.get(name);
    }

    public @Nullable ShadowGroup<S> nextShadowGroup(String name) {
        return nextShadowGroups.get(name);
    }

    public ShadowGroup<S> shadow(Iterable<? extends S> symbols) {
        return this.symbolsByName.overrideWith(
            PMultimap.groupBy(symbols, JElementSymbol::getSimpleName),
            nextShadowGroups,
            (shadows, k) -> shadows.plus(k, this),
            ShadowGroup::new
        );
    }

    public ShadowGroup<S> merge(Iterable<? extends S> symbols) {
        return new ShadowGroup<>(this.symbolsByName.appendAllGroupingBy(symbols, JElementSymbol::getSimpleName), nextShadowGroups);
    }

    public static <S extends JElementSymbol> ShadowGroup<S> root(Iterable<? extends S> symbols) {
        return new ShadowGroup<>(PMultimap.groupBy(symbols, JElementSymbol::getSimpleName),
                                 HashTreePMap.empty());
    }

}
