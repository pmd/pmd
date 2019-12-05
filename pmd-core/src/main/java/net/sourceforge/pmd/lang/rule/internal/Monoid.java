/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.Set;
import java.util.function.BinaryOperator;

/**
 * Describes an associative binary operation on a type {@code <U>},
 * that has an identity element.
 *
 * @param <U> Domain of the operation
 */
interface Monoid<U> extends BinaryOperator<U> {

    /** Combine two U, in a way consistent with {@link #zero()}. */
    @Override
    U apply(U u, U u2);


    /** Neutral element for the {@link #apply} operation. */
    U zero();


    /** Produces a new set, the union of both arguments. */
    static <T> Monoid<Set<T>> forSet() {
        return (Monoid<Set<T>>) MonoidImplUtils.PSET_MONOID;
    }


    /** Accumulates the right argument into the left one (mutating it). */
    static <T> Monoid<Set<T>> forMutableSet() {
        return (Monoid<Set<T>>) MonoidImplUtils.MSET_MONOID;
    }


}
