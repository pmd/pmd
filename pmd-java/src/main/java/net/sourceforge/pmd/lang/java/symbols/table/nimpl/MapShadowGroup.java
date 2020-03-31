/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.nimpl;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;

public final class MapShadowGroup<S extends JElementSymbol> implements ShadowGroup<S> {

    static MapShadowGroup EMPTY = new MapShadowGroup(null, PMultimap.empty(), HashTreePMap.empty());

    private final @Nullable ShadowGroup<S> fallback;
    private final PMultimap<String, S> symbolsByName;
    private final PMap<String, ShadowGroup<S>> nextShadowGroups;

    private MapShadowGroup(@Nullable ShadowGroup<S> fallback,
                           PMultimap<String, S> symbolsByName,
                           PMap<String, ShadowGroup<S>> nextShadowGroups) {
        this.fallback = fallback;
        this.symbolsByName = symbolsByName;
        this.nextShadowGroups = nextShadowGroups;
    }

    @Override
    public @NonNull List<S> resolve(String name) {
        List<S> res = symbolsByName.get(name);
        return res.isEmpty() && fallback != null ? fallback.resolve(name) : res;
    }

    @Override
    public @Nullable ShadowGroup<S> nextShadowGroup(String name) {
        return nextShadowGroups.getOrDefault(name, fallback);
    }

    @Override
    public String toString() {
        return symbolsByName.toString();
    }


    // construction/transformation methods


    MapShadowGroup<S> shadowWith(PMultimap<String, S> symbols) {
        return this.symbolsByName.overrideWith(
            symbols,
            nextShadowGroups,
            (shadows, k) -> shadows.plus(k, this),
            (byName, nextShadows) -> new MapShadowGroup<>(this.fallback, byName, nextShadows)
        );
    }

    ShadowGroup<S> merge(PMultimap<String, S> symbols) {
        return new MapShadowGroup<>(this.fallback, this.symbolsByName.appendAll(symbols), nextShadowGroups);
    }

    ShadowGroup<S> merge(S symbol) {
        return new MapShadowGroup<>(this.fallback, this.symbolsByName.appendValue(symbol.getSimpleName(), symbol), nextShadowGroups);
    }

    static <S extends JElementSymbol> MapShadowGroup<S> root(ShadowGroup<S> fallback,
                                                          PMultimap<String, S> symbols) {
        return new MapShadowGroup<>(fallback, symbols, HashTreePMap.empty());
    }

    static <S extends JElementSymbol> ShadowGroup<S> root(ShadowGroup<S> fallback, S symbol) {
        return root(fallback, PMultimap.singleton(symbol.getSimpleName(), symbol));
    }

    static <S extends JElementSymbol> ShadowGroup<S> empty() {
        return EMPTY;
    }

}
