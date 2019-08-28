/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;

/**
 * Describes a particular {@linkplain #apply(Object, Object) pure operation} on a
 * type {@code <U>}. That operation must have a {@linkplain #zero() neutral element},
 * meaning the following must hold for all {@code u : U}, and for all {@code z : U}
 * such that {@code z.equals(zero())}:
 * <pre>
 *  apply(u, z).equals(u)
 *  apply(z, u).equals(u)
 * </pre>
 *
 * @param <U> Domain of the operation
 */
interface Monoid<U> extends BinaryOperator<U> {

    /** Combine two U, in a way consistent with {@link #zero()}. This method must not produce side-effects. */
    @Override
    U apply(U u, U u2);


    /** Neutral element for the {@link #apply} operation. */
    U zero();


    static <T> Monoid<Set<T>> forSet() {
        return (Monoid<Set<T>>) MonoidImpl.SET_MONOID;
    }


    static <T> Monoid<List<T>> forList() {
        return (Monoid<List<T>>) MonoidImpl.LIST_MONOID;
    }
}
