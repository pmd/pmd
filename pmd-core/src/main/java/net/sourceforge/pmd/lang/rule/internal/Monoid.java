/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;


public interface Monoid<U> {

    /**
     * Combine two U, in a way consistent with {@link #zero()}.
     * This operation must satisfy the following requirement:
     * <pre>
     *  Neutral element: combine(u, zero()).equals(u)
     *                   combine(zero(), u).equals(u)
     * </pre>
     */
    U combine(U l, U r);


    /** Neutral element for the {@link #combine} operation. */
    U zero();


    static <T> Monoid<Set<T>> forSet() {
        return new Monoid<Set<T>>() {
            @Override
            public Set<T> combine(Set<T> l, Set<T> r) {
                if (l == Collections.EMPTY_SET) {
                    return r;
                } else if (r == Collections.EMPTY_SET) {
                    return l;
                }
                HashSet<T> more = new HashSet<>(l);
                more.addAll(r);
                return more;
            }

            @Override
            public Set<T> zero() {
                return Collections.emptySet();
            }
        };
    }


    static <T> Monoid<Stream<T>> forStream() {
        return new Monoid<Stream<T>>() {
            @Override
            public Stream<T> combine(Stream<T> l, Stream<T> r) {
                return Stream.concat(l, r);
            }

            @Override
            public Stream<T> zero() {
                return Stream.empty();
            }
        };
    }


    static <T> Monoid<Supplier<T>> forSupplier(Monoid<T> monoid) {
        return new Monoid<Supplier<T>>() {
            @Override
            public Supplier<T> combine(Supplier<T> l, Supplier<T> r) {
                return () -> monoid.combine(l.get(), r.get());
            }

            @Override
            public Supplier<T> zero() {
                return monoid::zero;
            }
        };
    }


    static <T> Monoid<List<T>> forList() {
        return new Monoid<List<T>>() {
            @Override
            public List<T> combine(List<T> l, List<T> r) {
                if (l == Collections.EMPTY_LIST) {
                    return r;
                } else if (r == Collections.EMPTY_LIST) {
                    return l;
                }
                ArrayList<T> more = new ArrayList<>(l.size() + r.size());
                more.addAll(l);
                more.addAll(r);
                return more;
            }

            @Override
            public List<T> zero() {
                return Collections.emptyList();
            }
        };
    }
}
