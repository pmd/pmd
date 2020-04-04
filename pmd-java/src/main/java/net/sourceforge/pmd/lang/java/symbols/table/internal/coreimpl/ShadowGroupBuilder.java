/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal.coreimpl;


import static net.sourceforge.pmd.lang.java.symbols.table.internal.coreimpl.CoreResolvers.multimapResolver;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.coreimpl.CoreResolvers.singleton;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.coreimpl.CoreResolvers.singularMapResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import net.sourceforge.pmd.lang.java.symbols.table.internal.coreimpl.MostlySingularMultimap.Builder;
import net.sourceforge.pmd.lang.java.symbols.table.internal.coreimpl.MostlySingularMultimap.MapMaker;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Utility to build shadow groups for a given type.
 *
 * @param <S> Type of symbols for the built shadow groups.
 */
public abstract class ShadowGroupBuilder<S> {

    private final MapMaker<String> mapMaker = this::copyStrategy;

    public ShadowGroupBuilder() {
    }

    private MostlySingularMultimap.Builder<String, S> newMapBuilder() {
        return MostlySingularMultimap.newBuilder(mapMaker);
    }

    protected <V> Map<String, V> copyStrategy(Map<String, V> m) {
        return new HashMap<>(m);
    }


    protected abstract String getSimpleName(S sym);


    public static <S> ShadowGroup<S> rootGroup() {
        return RootShadowGroup.empty();
    }

    public ShadowGroup<S> augment(ShadowGroup<S> parent, boolean shadowBarrier, ResolverBuilder symbols) {
        if (symbols.isEmpty() && !shadowBarrier) {
            return parent;
        }
        return new SimpleShadowGroup<>(parent, shadowBarrier, symbols.build());
    }

    public ShadowGroup<S> augment(ShadowGroup<S> parent, boolean shadowBarrier, NameResolver<S> resolver) {
        return new SimpleShadowGroup<>(parent, shadowBarrier, resolver);
    }

    public ShadowGroup<S> augment(ShadowGroup<S> parent, boolean shadowBarrier, S symbol) {
        return new SimpleShadowGroup<>(parent, shadowBarrier, singleton(getSimpleName(symbol), symbol));
    }

    public ShadowGroup<S> augmentWithCache(ShadowGroup<S> parent, boolean shadowBarrier, NameResolver<S> resolver) {
        return augmentWithCache(parent, shadowBarrier, new HashMap<>(), resolver);
    }

    public ShadowGroup<S> augmentWithCache(ShadowGroup<S> parent, boolean shadowBarrier, Map<String, List<S>> cacheMap, NameResolver<S> resolver) {
        return new CachedShadowGroup<>(parent, cacheMap, resolver, shadowBarrier);
    }


    // default the shadowBarrier param to true

    public ShadowGroup<S> shadow(ShadowGroup<S> parent, ResolverBuilder resolver) {
        return augment(parent, true, resolver);
    }

    public ShadowGroup<S> shadow(ShadowGroup<S> parent, NameResolver<S> resolver) {
        return augment(parent, true, resolver);
    }

    public ShadowGroup<S> shadow(ShadowGroup<S> parent, S symbol) {
        return augment(parent, true, symbol);
    }


    // convenience

    public <N> ResolverBuilder groupByName(Iterable<? extends N> input, Function<? super N, ? extends S> symbolFetcher) {
        return new ResolverBuilder(newMapBuilder().groupBy(CollectionUtil.map(input, symbolFetcher), this::getSimpleName));
    }

    public ResolverBuilder groupByName(Iterable<? extends S> tparams) {
        return new ResolverBuilder(newMapBuilder().groupBy(tparams, this::getSimpleName));
    }

    public NameResolver<S> groupByName(S sym) {
        return singleton(getSimpleName(sym), sym);
    }


    public class ResolverBuilder {

        private final MostlySingularMultimap.Builder<String, S> myBuilder;

        public ResolverBuilder(Builder<String, S> myBuilder) {
            this.myBuilder = myBuilder;
        }

        public ResolverBuilder() {
            this.myBuilder = newMapBuilder();
        }

        public ResolverBuilder append(S sym) {
            myBuilder.appendValue(getSimpleName(sym), sym);
            return this;
        }

        public Map<String, List<S>> getMutableMap() {
            return myBuilder.getMutableMap();
        }

        public NameResolver<S> build() {
            if (isEmpty()) {
                return CoreResolvers.emptyResolver();
            } else if (myBuilder.isSingular()) {
                Map<String, S> singular = myBuilder.buildAsSingular();
                assert singular != null;
                if (singular.size() == 1) {
                    Entry<String, S> pair = singular.entrySet().iterator().next();
                    return singleton(pair.getKey(), pair.getValue());
                } else {
                    return singularMapResolver(singular);
                }
            }
            return multimapResolver(myBuilder.build());
        }

        public boolean isEmpty() {
            return myBuilder.isEmpty();
        }
    }
}
