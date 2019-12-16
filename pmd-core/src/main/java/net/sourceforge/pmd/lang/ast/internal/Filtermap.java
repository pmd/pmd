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

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Combined filter/map predicate. Cannot accept null values.
 */
@FunctionalInterface
interface Filtermap<I, O> extends Function<@NonNull I, @Nullable O>, Predicate<@NonNull I> {


    Filtermap<Node, Node> NODE_IDENTITY = emptyFilter();


    /**
     * Returns a null value if the filter accepts the value. Otherwise
     * returns the transformed value.
     */
    @Override
    @Nullable O apply(@NonNull I i);


    @Override
    default boolean test(@NonNull I i) {
        return apply(i) != null;
    }

    /** Filter an iterator. */
    default Iterator<O> filterMap(Iterator<I> iter) {
        return IteratorUtil.mapNotNull(iter, this);
    }


    /** Compose a new Filtermap, coalescing null values. */
    default <R> Filtermap<I, R> thenApply(Function<@NonNull ? super O, @Nullable ? extends R> then) {
        Objects.requireNonNull(then);
        return i -> {
            O o = this.apply(i);
            return o == null ? null : then.apply(o);
        };
    }


    default <R> Filtermap<I, R> thenCast(Class<R> rClass) {
        return thenApply(isInstance(rClass));
    }


    static <I> Filtermap<I, I> emptyFilter() {
        return new Filtermap<I, I>() {
            @Override
            public I apply(@NonNull I i) {
                return i;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <R> Filtermap<I, R> thenApply(Function<@NonNull ? super I, @Nullable ? extends R> then) {
                return then instanceof Filtermap ? (Filtermap<I, R>) then : then::apply;
            }

            @Override
            public Iterator<I> filterMap(Iterator<I> iter) {
                return iter;
            }
        };
    }


    static <I> Filtermap<I, I> filter(Predicate<? super I> pred) {
        return i -> pred.test(i) ? i : null;
    }


    static <I, O> Filtermap<I, O> isInstance(Class<O> oClass) {
        return i -> oClass.isInstance(i) ? oClass.cast(i) : null;
    }

}
