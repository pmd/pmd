/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;


import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.IteratorUtil;

/**
 * Combined filter/map predicate. Cannot accept null values.
 *
 * @param <I> Input type, contravariant
 * @param <O> Output type, covariant
 */
@FunctionalInterface
interface Filtermap<I, O> extends Function<@NonNull I, @Nullable O>, Predicate<@NonNull I> {


    Filtermap<Node, Node> NODE_IDENTITY = identityFilter();


    /**
     * Returns a null value if the filter accepts the value. Otherwise
     * returns the transformed value. MUST return null for null parameter.
     */
    @Override
    @Nullable O apply(@Nullable I i);


    @Override
    default boolean test(@Nullable I i) {
        return apply(i) != null;
    }

    /** Filter an iterator. */
    default Iterator<O> filterMap(Iterator<? extends I> iter) {
        return applyIterator(iter, this);
    }

    static <I, O> Iterator<O> applyIterator(Iterator<? extends I> iter, Filtermap<? super I, ? extends O> filtermap) {
        return IteratorUtil.mapNotNull(iter, filtermap);
    }


    /** Compose a new Filtermap, coalescing null values. */
    default <R> Filtermap<I, R> thenApply(Function<@NonNull ? super O, @Nullable ? extends R> then) {
        Objects.requireNonNull(then);
        return i -> {
            if (i == null) {
                return null;
            }
            O o = this.apply(i);
            return o == null ? null : then.apply(o);
        };
    }


    default <R> Filtermap<I, R> thenCast(Class<? extends R> rClass) {
        return thenApply(isInstance(rClass));
    }


    default Filtermap<I, O> thenFilter(Predicate<? super O> rClass) {
        return thenApply(filter(rClass));
    }


    static <I> Filtermap<I, I> identityFilter() {
        return new Filtermap<I, I>() {
            @Override
            public I apply(@Nullable I i) {
                return i;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <R> Filtermap<I, R> thenApply(Function<@NonNull ? super I, @Nullable ? extends R> then) {
                return then instanceof Filtermap ? (Filtermap<I, R>) then : Filtermap.super.thenApply(then);
            }

            @Override
            @SuppressWarnings("unchecked")
            public Iterator<I> filterMap(Iterator<? extends I> iter) {
                return (Iterator<I>) iter;
            }

            @Override
            public String toString() {
                return "IdentityFilter";
            }
        };
    }


    static <I extends O, O> Filtermap<I, O> filter(Predicate<? super @NonNull I> pred) {
        return i -> i != null && pred.test(i) ? i : null;
    }


    static <I, O> Filtermap<I, O> isInstance(Class<? extends O> oClass) {
        if (oClass == Node.class) {
            return (Filtermap<I, O>) NODE_IDENTITY;
        }

        return new Filtermap<I, O>() {
            @Override
            @SuppressWarnings("unchecked")
            public @Nullable O apply(@Nullable I i) {
                return oClass.isInstance(i) ? (O) i : null;
            }

            @Override
            public String toString() {
                return "IsInstance[" + oClass + "]";
            }
        };
    }

}
