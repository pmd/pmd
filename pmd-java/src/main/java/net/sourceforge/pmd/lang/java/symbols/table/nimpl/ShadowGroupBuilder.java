/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.nimpl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.sourceforge.pmd.lang.java.symbols.table.nimpl.MostlySingularMultimap.Builder;
import net.sourceforge.pmd.util.CollectionUtil;

public class ShadowGroupBuilder<S> {

    private final Function<? super S, String> namer;

    public ShadowGroupBuilder(Function<? super S, String> namer) {
        this.namer = namer;
    }


    public ShadowGroup<S> augment(ShadowGroup<S> parent, boolean shadowBarrier, Builder<String, S> symbols) {
        if (symbols.isEmpty() && !shadowBarrier) {
            return parent;
        }
        return new SimpleShadowGroup<>(parent, shadowBarrier, Resolvers.mapResolver(symbols));
    }

    public ShadowGroup<S> augment(ShadowGroup<S> parent, boolean shadowBarrier, NameResolver<S> resolver) {
        return new SimpleShadowGroup<>(parent, shadowBarrier, resolver);
    }

    public ShadowGroup<S> augment(ShadowGroup<S> parent, boolean shadowBarrier, S symbol) {
        return new SimpleShadowGroup<>(parent, shadowBarrier, Resolvers.singleton(namer.apply(symbol), symbol));
    }

    public ShadowGroup<S> augmentWithCache(ShadowGroup<S> parent, boolean shadowBarrier, NameResolver<S> resolver) {
        return augmentWithCache(parent, shadowBarrier, new HashMap<>(), resolver);
    }

    public ShadowGroup<S> augmentWithCache(ShadowGroup<S> parent, boolean shadowBarrier, Map<String, List<S>> cacheMap, NameResolver<S> resolver) {
        return new CachedShadowGroup<>(parent, cacheMap, resolver, shadowBarrier);
    }


    // default the shadowBarrier param to true

    public ShadowGroup<S> shadow(ShadowGroup<S> parent, Builder<String, S> symbols) {
        return augment(parent, true, symbols);
    }

    public ShadowGroup<S> shadow(ShadowGroup<S> parent, NameResolver<S> resolver) {
        return augment(parent, true, resolver);
    }

    public ShadowGroup<S> shadow(ShadowGroup<S> parent, S symbol) {
        return augment(parent, true, symbol);
    }


    // convenience

    public <N> Builder<String, S> groupByName(Iterable<? extends N> input, Function<? super N, ? extends S> symbolFetcher) {
        return Resolvers.<S>newMapBuilder().groupBy(CollectionUtil.map(input, symbolFetcher), namer);
    }

    public Builder<String, S> groupByName(Iterable<? extends S> tparams) {
        return Resolvers.<S>newMapBuilder().groupBy(tparams, namer);
    }

    public NameResolver<S> groupByName(S sym) {
        return Resolvers.singleton(namer.apply(sym), sym);
    }
}
