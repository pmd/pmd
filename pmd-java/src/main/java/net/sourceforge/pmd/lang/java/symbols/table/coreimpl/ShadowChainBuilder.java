/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;


import static net.sourceforge.pmd.lang.java.symbols.table.coreimpl.CoreResolvers.multimapResolver;
import static net.sourceforge.pmd.lang.java.symbols.table.coreimpl.CoreResolvers.singleton;
import static net.sourceforge.pmd.lang.java.symbols.table.coreimpl.CoreResolvers.singularMapResolver;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.MostlySingularMultimap.Builder;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.MostlySingularMultimap.MapMaker;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Build a shadow chain for some type.
 *
 * <p>Implementing this framework means implementing {@link NameResolver}s for
 * each relevant way that a declaration may be brought in scope, then figuring
 * out the correct way these resolvers should be linked into a ShadowChain.
 * Shadow chain builders just give some utility methods to make the linking
 * process more straightforward.
 *
 * @param <S> Type of symbols
 * @param <I> Type of scope tags
 */
public abstract class ShadowChainBuilder<S, I> {

    private final MapMaker<String> mapMaker = this::copyToMutable;

    MostlySingularMultimap.Builder<String, S> newMapBuilder() {
        return MostlySingularMultimap.newBuilder(mapMaker);
    }

    /**
     * Copy the given map into a new mutable map. This is provided
     * as a hook to experiment with alternative map implementations
     * easily, eg tries, or specialized maps.
     */
    protected <V> Map<String, V> copyToMutable(Map<String, V> m) {
        return new LinkedHashMap<>(m);
    }

    /** Returns the name with which the given symbol should be indexed. */
    public abstract String getSimpleName(S sym);

    /** Returns the singleton for the chain root. */
    public static <S, I> ShadowChainNode<S, I> rootGroup() {
        return ShadowChainRoot.empty();
    }

    // #augment overloads wrap a resolver into a new chain node

    public ShadowChainNode<S, I> augment(ShadowChainNode<S, I> parent, boolean shadowBarrier, I scopeTag, ResolverBuilder symbols) {
        if (isPrunable(parent, shadowBarrier, symbols.isEmpty())) {
            return parent;
        }
        return new ShadowChainNodeBase<>(parent, shadowBarrier, scopeTag, symbols.build());
    }

    public ShadowChainNode<S, I> augment(ShadowChainNode<S, I> parent, boolean shadowBarrier, I scopeTag, NameResolver<S> resolver) {
        if (isPrunable(parent, shadowBarrier, resolver.isDefinitelyEmpty())) {
            return parent;
        }
        return new ShadowChainNodeBase<>(parent, shadowBarrier, scopeTag, resolver);
    }

    // prunes empty nodes if doing so will not alter results
    private boolean isPrunable(ShadowChainNode<S, I> parent, boolean shadowBarrier, boolean definitelyEmpty) {
        return definitelyEmpty && (!shadowBarrier
            || parent.getResolver().isDefinitelyEmpty() && parent.isShadowBarrier());
    }

    public ShadowChainNode<S, I> augment(ShadowChainNode<S, I> parent, boolean shadowBarrier, I scopeTag, S symbol) {
        return new ShadowChainNodeBase<>(parent, shadowBarrier, scopeTag, singleton(getSimpleName(symbol), symbol));
    }

    // #__WithCache use a cache for resolved symbols
    // Use this for expensive resolvers, instead of caching in the
    // resolver itself (the chain node will cache the results of the
    // parents too)

    public ShadowChainNode<S, I> augmentWithCache(ShadowChainNode<S, I> parent, boolean shadowBarrier, I scopeTag, NameResolver<S> resolver) {
        return new CachingShadowChainNode<>(parent, new HashMap<>(), resolver, shadowBarrier, scopeTag);
    }

    public ShadowChainNode<S, I> shadowWithCache(ShadowChainNode<S, I> parent,
                                                 I scopeTag,
                                                 // this map will be used as the cache without copy,
                                                 // it may contain initial bindings, which is only
                                                 // valid if the built group is a shadow barrier, which
                                                 // is why this parameter is defaulted.
                                                 Map<String, List<S>> cacheMap,
                                                 NameResolver<S> resolver) {
        return new CachingShadowChainNode<>(parent, cacheMap, resolver, true, scopeTag);
    }


    // #shadow overloads default the shadowBarrier param to true

    public ShadowChainNode<S, I> shadow(ShadowChainNode<S, I> parent, I scopeTag, ResolverBuilder resolver) {
        return augment(parent, true, scopeTag, resolver);
    }

    public ShadowChainNode<S, I> shadow(ShadowChainNode<S, I> parent, I scopeTag, NameResolver<S> resolver) {
        return augment(parent, true, scopeTag, resolver);
    }

    public ShadowChainNode<S, I> shadow(ShadowChainNode<S, I> parent, I scopeTag, S symbol) {
        return augment(parent, true, scopeTag, symbol);
    }


    // convenience to build name resolvers

    public <N> ResolverBuilder groupByName(Iterable<? extends N> input, Function<? super N, ? extends S> symbolFetcher) {
        return new ResolverBuilder(newMapBuilder().groupBy(CollectionUtil.map(input, symbolFetcher), this::getSimpleName));
    }

    public ResolverBuilder groupByName(Iterable<? extends S> tparams) {
        return new ResolverBuilder(newMapBuilder().groupBy(tparams, this::getSimpleName));
    }

    public NameResolver<S> groupByName(S sym) {
        return singleton(getSimpleName(sym), sym);
    }

    /**
     * Helper to build a new name resolver. The internal data structure
     * optimises for the case where there are no name collisions, which
     * is a good trade for Java.
     */
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

        public ResolverBuilder appendWithoutDuplicate(S sym) {
            myBuilder.appendValue(getSimpleName(sym), sym, true);
            return this;
        }

        public ResolverBuilder overwrite(S sym) {
            myBuilder.replaceValue(getSimpleName(sym), sym);
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
