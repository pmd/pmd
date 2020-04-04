/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal.coreimpl;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.util.OptionalBool;

class SimpleShadowGroup<S> implements ShadowGroup<S> {

    protected final NameResolver<S> resolver;
    protected final @NonNull ShadowGroup<S> parent;
    private final boolean shadowBarrier;


    SimpleShadowGroup(@NonNull ShadowGroup<S> fallback, boolean shadowBarrier, NameResolver<S> resolver) {
        this.parent = fallback;
        this.shadowBarrier = shadowBarrier;
        this.resolver = resolver;
    }

    @Override
    public final boolean isShadowBarrier() {
        return shadowBarrier;
    }

    @Override
    public @Nullable ShadowGroup<S> nextShadowGroup(String simpleName) {
        boolean shadowBarrierCrossed = isShadowBarrier();
        ShadowGroup<S> parent = this.parent;
        OptionalBool parentKnows;
        do {
            shadowBarrierCrossed |= parent.isShadowBarrier();

            if (parent instanceof SimpleShadowGroup) {
                SimpleShadowGroup<S> p = (SimpleShadowGroup<S>) parent;
                parentKnows = p.knowsSymbol(simpleName);
                parent = p.parent;
            } else {
                // root
                return null;
            }
        } while (parentKnows == OptionalBool.NO || !shadowBarrierCrossed);

        return parent;
    }

    /** Doesn't ask the parents. */
    protected OptionalBool knowsSymbol(String simpleName) {
        return resolver.knows(simpleName);
    }

    @Override
    public @NonNull List<S> resolve(String name) {
        List<S> res = resolver.resolveHere(name);
        if (res.isEmpty()) {
            return parent.resolve(name);
        } else if (!isShadowBarrier()) {
            return ConsList.cons(res, parent.resolve(name));
        }
        return res;
    }

    @Override
    public S resolveFirst(String name) {
        S s = resolver.resolveFirst(name);
        return s != null ? s : parent.resolveFirst(name);
    }

    @Override
    public String toString() {
        return resolver.toString();
    }

}
