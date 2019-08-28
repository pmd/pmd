/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.List;
import java.util.Set;

/**
 * Describes a particular {@linkplain #combine(Object, Object) pure operation} on a
 * type {@code <U>}. That operation must have a {@linkplain #zero() neutral element},
 * meaning the following must hold for all {@code u : U}:
 * <pre>
 *  combine(u, zero()).equals(u)
 *  combine(zero(), u).equals(u)
 * </pre>
 *
 * @param <U> Domain of the operation
 */
public interface Monoid<U> {

    /** Combine two U, in a way consistent with {@link #zero()}. This method must not produce side-effects. */
    U combine(U l, U r);


    /** Neutral element for the {@link #combine} operation. */
    U zero();


    static <T> Monoid<Set<T>> forSet() {
        return (Monoid<Set<T>>) MonoidImpl.SET_MONOID;
    }


    static <T> Monoid<List<T>> forList() {
        return (Monoid<List<T>>) MonoidImpl.LIST_MONOID;
    }
}
