/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.Set;
import java.util.function.BinaryOperator;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Describes an associative, idempotent binary operation on a type
 * {@code <U>}, that has an identity element.
 *
 * <p>This is used to merge values in a {@link LatticeRelation}.
 * A proper monoid doesn't necessarily have the idempotence property,
 * but we require this to avoid dealing with diamond situations explicitly.
 * This means, merging the same value several times doesn't matter.
 *
 * @param <U> Domain of the operation
 */
interface IdMonoid<@NonNull U> extends BinaryOperator<U> {

    /**
     * Combine two U, in a way consistent with {@link #zero()}.
     * These are the monoid identities:
     * <pre>{@code
     *     apply(zero(), U) == apply(U, zero()) == U        (identity element)
     *     apply(apply(U, V), W) == apply(U, apply(V, W))   (associativity)
     * }</pre>
     * Additionally this operation must be idempotent:
     * <pre>{@code
     *     apply(U, U) == U
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
    static <T> IdMonoid<Set<T>> forSet() {
        return (IdMonoid<Set<T>>) MonoidImplUtils.PSET_MONOID;
    }


    /** Accumulates the right argument into the left one (mutating it). */
    @SuppressWarnings("unchecked")
    static <T> IdMonoid<Set<T>> forMutableSet() {
        return (IdMonoid<Set<T>>) MonoidImplUtils.MSET_MONOID;
    }


}
