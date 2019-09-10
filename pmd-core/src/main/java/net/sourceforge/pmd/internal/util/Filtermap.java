/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;


import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Combined filter/map predicate. Cannot accept null values.
 */
@FunctionalInterface
public interface Filtermap<I, O> extends Function<@NonNull I, @Nullable O> {


    Filtermap<Node, Node> NODE_IDENTITY = emptyFilter();


    /**
     * Returns a null value if the filter accepts the value. Otherwise
     * returns the transformed value.
     */
    @Override
    @Nullable O apply(@NonNull I i);


    default Iterator<O> filterMap(Iterator<I> iter) {
        return IteratorUtil.mapNotNull(iter, this);
    }


    default <R> Filtermap<I, R> then(Filtermap<? super O, ? extends R> then) {
        return i -> {
            O o = this.apply(i);
            return o == null ? null : then.apply(o);
        };
    }


    default <R> Filtermap<I, R> thenCast(Class<R> rClass) {
        return then(isInstance(rClass));
    }


    static <I> Filtermap<I, I> emptyFilter() {
        return new Filtermap<I, I>() {
            @Override
            public I apply(@NonNull I i) {
                return i;
            }

            @Override
            public <R> Filtermap<I, R> then(Filtermap<? super I, ? extends R> then) {
                return (Filtermap<I, R>) then;
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
