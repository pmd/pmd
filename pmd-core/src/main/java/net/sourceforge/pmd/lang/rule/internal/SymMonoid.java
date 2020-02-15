/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.Set;
import java.util.function.BinaryOperator;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Describes an operation used to combine values in a {@link LatticeRelation}.
 * This operation must satisfy some properties, explained on {@link #apply(Object, Object) apply}.
 *
 * @param <U> Domain of the operation
 */
interface SymMonoid<@NonNull U> extends BinaryOperator<U> {

    /**
     * Combine two U, in a way consistent with {@link #zero()}.
     * These are the monoid identities:
     * <pre>{@code
     *     apply(zero(), U) == apply(U, zero()) == U        (identity element)
     *     apply(apply(U, V), W) == apply(U, apply(V, W))   (associativity)
     * }</pre>
     * This operation must also be commutative, because ordering
     * of lattice nodes is unspecified:
     * <pre>{@code
     *   apply(U, V) == apply(V, U)
     * }</pre>
     * The latter property explains the choice of name ("Sym").
     */
    @Override
    U apply(U u, U u2);


    /** Identity element for the {@link #apply} operation. */
    U zero();


    /**
     * Transform a value of type U before performing a reduction.
     * This is to allow several monoids for the same type to cooperate,
     * in case one needs a specific implementation.
     */
    default U lift(U u) {
        return apply(zero(), u);
    }


    /** Apply produces a new set, the union of both arguments. */
    @SuppressWarnings("unchecked")
    static <T> SymMonoid<Set<T>> forSet() {
        return (SymMonoid<Set<T>>) MonoidImplUtils.PSET_MONOID;
    }


    /** Accumulates the right argument into the left one (mutating it). */
    @SuppressWarnings("unchecked")
    static <T> SymMonoid<Set<T>> forMutableSet() {
        return (SymMonoid<Set<T>>) MonoidImplUtils.MSET_MONOID;
    }


}
