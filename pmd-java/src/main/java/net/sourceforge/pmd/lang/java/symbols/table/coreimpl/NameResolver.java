/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * Name resolvers are strategies backing {@link ShadowChain}s. They have
 * no information about outer context, instead the structure of the shadow
 * group chain handles that.
 *
 * @param <S> Type of symbols
 */
public interface NameResolver<S> {

    /**
     * Returns all symbols known by this resolver that have the given
     * simple name. Depending on language semantics, finding several
     * symbols may mean there is ambiguity. If no such symbol is known,
     * returns an empty list.
     *
     * @param simpleName Simple name
     */
    @NonNull
    List<S> resolveHere(String simpleName);


    /**
     * Resolves the first symbol that would be part of the list yielded
     * by {@link #resolveHere(String)} for the given name. If the list
     * would be empty (no such symbol is known), returns null.
     */
    default @Nullable S resolveFirst(String simpleName) {
        List<S> result = resolveHere(simpleName);
        return result.isEmpty() ? null : result.get(0);
    }


    /**
     * Returns whether this resolver knows if it has a declaration for
     * the given name. If the result is NO, then resolveFirst MUST be null,
     * if the result is YES, then resolveFirst MUST be non-null. Otherwise
     * we don't know.
     */
    default @NonNull OptionalBool knows(String simpleName) {
        return OptionalBool.UNKNOWN;
    }

    /** Returns true if this resolver knows it cannot resolve anything. */
    default boolean isDefinitelyEmpty() {
        return false;
    }


    /** Please implement toString to ease debugging. */
    @Override
    String toString();

    /**
     * Returns a resolver that concatenates the results of every resolver
     * in the given list.
     *
     * @param resolvers Resolvers
     * @param <T>       Type of symbol
     */
    static <T> NameResolver<T> composite(List<? extends NameResolver<? extends T>> resolvers) {
        if (resolvers.isEmpty()) {
            return CoreResolvers.emptyResolver();
        }
        return new NameResolver<T>() {
            @Override
            public @NonNull List<T> resolveHere(String simpleName) {
                List<T> result = Collections.emptyList();
                for (NameResolver<? extends T> r : resolvers) {
                    List<? extends T> ts = r.resolveHere(simpleName);
                    if (!ts.isEmpty()) {
                        result = CollectionUtil.concatView(result, ts);
                    }
                }
                return result;
            }

            @Override
            public @Nullable T resolveFirst(String simpleName) {
                for (NameResolver<? extends T> r : resolvers) {
                    T t = r.resolveFirst(simpleName);
                    if (t != null) {
                        return t;
                    }
                }
                return null;
            }

            @Override
            public String toString() {
                return "Composite[" + resolvers + "]";
            }
        };
    }


    /**
     * A base class for resolvers that know at most one symbol for any
     * given name. This means {@link #resolveHere(String)} may delegate
     * to {@link #resolveFirst(String)}, for implementation simplicity.
     * This is also a marker interface used to optimise some things
     * internally.
     */
    interface SingleNameResolver<S> extends NameResolver<S> {

        @Override
        default @NonNull List<S> resolveHere(String simpleName) {
            return CollectionUtil.listOfNotNull(resolveFirst(simpleName));
        }


        // make it abstract
        @Override
        @Nullable S resolveFirst(String simpleName);
    }
}
