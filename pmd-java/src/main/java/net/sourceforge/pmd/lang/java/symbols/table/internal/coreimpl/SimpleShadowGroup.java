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
    public @NonNull ShadowGroup<S> getParent() {
        return parent;
    }


    @Override
    public @Nullable ShadowGroup<S> nextShadowGroup(String simpleName) {
        // this is the group that answered the "resolve" call
        ShadowGroup<S> answerer = nextGroupThatKnows(this, simpleName, false);
        if (answerer == null) {
            return null;
        }
        // we want the next one, to get different results
        return nextGroupThatKnows(answerer.getParent(), simpleName, true);
    }

    // inclusive of the parameter
    private static <S> @Nullable ShadowGroup<S> nextGroupThatKnows(@Nullable ShadowGroup<S> group, String name, boolean acceptUnknown) {
        ShadowGroup<S> parent = group;
        while (parent != null && !definitelyKnows(parent, name, acceptUnknown)) {
            parent = parent.getParent();
        }
        return parent;
    }

    private static boolean definitelyKnows(@NonNull ShadowGroup<?> group, String name, boolean acceptUnknown) {
        if (group instanceof SimpleShadowGroup) {
            OptionalBool opt = ((SimpleShadowGroup<?>) group).knowsSymbol(name);
            if (opt.isKnown()) {
                return opt.isTrue();
            } else {
                if (acceptUnknown) {
                    return group.resolveFirst(name) != null;
                } else {
                    // note: this could also be an implementation mistake,
                    // whereby an indefinite resolver was not cached.
                    throw new IllegalStateException(
                        "Called nextShadowGroup without first querying resolve on " + group);
                }
            }
        } else {
            assert group instanceof RootShadowGroup : "Not a root shadow group? " + group;
            return false;
        }
    }

    /** Doesn't ask the parents. */
    protected OptionalBool knowsSymbol(String simpleName) {
        return resolver.knows(simpleName);
    }

    @Override
    public @NonNull List<S> resolve(String name) {
        List<S> res = resolver.resolveHere(name);
        handleResolverKnows(name, !res.isEmpty());
        if (res.isEmpty()) {
            return parent.resolve(name);
        } else if (!isShadowBarrier()) {
            return ConsList.cons(res, parent.resolve(name));
        }
        return res;
    }

    protected void handleResolverKnows(String name, boolean resolverKnows) {
        // to be overridden
    }

    @Override
    public S resolveFirst(String name) {
        S s = resolver.resolveFirst(name);
        handleResolverKnows(name, name != null);
        return s != null ? s : parent.resolveFirst(name);
    }

    @Override
    public String toString() {
        return resolver.toString();
    }

}
