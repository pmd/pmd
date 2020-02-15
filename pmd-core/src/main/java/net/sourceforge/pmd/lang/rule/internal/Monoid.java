/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.Set;
import java.util.function.BinaryOperator;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Describes an associative, binary operation on a type
 * {@code <U>}, that has an identity element.
 *
 * <p>This is used to merge values in a {@link LatticeRelation}.
 *
 * @param <U> Domain of the operation
 */
interface Monoid<@NonNull U> extends BinaryOperator<U> {

    /**
     * Combine two U, in a way consistent with {@link #zero()}.
     * These are the monoid identities:
     * <pre>{@code
     *     apply(zero(), U) == apply(U, zero()) == U        (identity element)
     *     apply(apply(U, V), W) == apply(U, apply(V, W))   (associativity)
     * }</pre>
     */
    @Override
    U apply(U u, U u2);


    /** Identity element for the {@link #apply} operation. */
    U zero();

    default U lift(U u) {
        return apply(zero(), u);
    }


    /** Apply produces a new set, the union of both arguments. */
    @SuppressWarnings("unchecked")
    static <T> Monoid<Set<T>> forSet() {
        return (Monoid<Set<T>>) MonoidImplUtils.PSET_MONOID;
    }


    /** Accumulates the right argument into the left one (mutating it). */
    @SuppressWarnings("unchecked")
    static <T> Monoid<Set<T>> forMutableSet() {
        return (Monoid<Set<T>>) MonoidImplUtils.MSET_MONOID;
    }


}
