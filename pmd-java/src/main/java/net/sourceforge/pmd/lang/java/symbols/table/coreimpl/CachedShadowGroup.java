/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.util.OptionalBool;

class CachedShadowGroup<S, I> extends ShadowChainNode<S, I> {

    private final Map<String, List<S>> cache;

    // contains YES/NO depending on whether *this* name resolver knew a
    // result when asked for it
    private final Map<String, OptionalBool> keysThatIKnow = new HashMap<>();

    protected CachedShadowGroup(@NonNull ShadowChain<S, I> parent,
                                Map<String, List<S>> known,
                                NameResolver<S> resolver,
                                boolean shadowBarrier,
                                I scopeTag) {
        super(parent, shadowBarrier, scopeTag, resolver);
        this.cache = known;
    }

    @Override
    public @NonNull List<S> resolve(String name) {
        List<S> result = cache.get(name);
        if (result != null) {
            return result;
        }
        result = super.resolve(name);
        cache.put(name, result);
        return result;
    }

    @Override
    protected void handleResolverKnows(String name, boolean resolverKnows) {
        keysThatIKnow.put(name, OptionalBool.definitely(resolverKnows));
    }

    @Override
    public S resolveFirst(String name) {
        List<S> result = cache.get(name);
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }
        S first = super.resolveFirst(name);
        if (first == null) {
            cache.put(name, Collections.emptyList());
        } else if (resolver instanceof NameResolver.SingleNameResolver && isShadowBarrier()) {
            cache.put(name, Collections.singletonList(first));
        }
        return first;
    }

    @Override
    protected OptionalBool knowsSymbol(String simpleName) {
        OptionalBool resolverKnows = resolver.knows(simpleName);
        if (resolverKnows.isKnown()) {
            return resolverKnows;
        } else {
            return keysThatIKnow.getOrDefault(simpleName, OptionalBool.UNKNOWN);
        }
    }

    @Override
    public String toString() {
        return "Cached("
            + "cache size=" + cache.size() + ", "
            + "resolver=" + super.toString()
            + ')';
    }
}
