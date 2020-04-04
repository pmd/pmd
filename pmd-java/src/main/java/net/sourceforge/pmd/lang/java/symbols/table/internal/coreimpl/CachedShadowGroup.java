/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal.coreimpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.util.OptionalBool;

class CachedShadowGroup<S> extends SimpleShadowGroup<S> {

    private final Map<String, List<S>> cache;

    protected CachedShadowGroup(@NonNull ShadowGroup<S> parent,
                                Map<String, List<S>> known,
                                NameResolver<S> resolver,
                                boolean shadowBarrier) {
        super(parent, shadowBarrier, resolver);
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
        List<S> cached = cache.get(simpleName);
        if (cached == null) {
            return super.knowsSymbol(simpleName); // ask resolver
        } else if (cached.isEmpty()) {
            return OptionalBool.NO;
        } else {
            return OptionalBool.YES;
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
