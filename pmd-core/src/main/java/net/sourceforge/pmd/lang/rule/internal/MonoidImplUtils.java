/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.LinkedHashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;


@SuppressWarnings("rawtypes")
final class MonoidImplUtils {

    private MonoidImplUtils() {
        // utility class
    }

    static final SymMonoid PSET_MONOID = psetMonoid();
    static final SymMonoid MSET_MONOID = mutableSetMonoid();

    @NonNull
    private static <T> SymMonoid<Set<T>> psetMonoid() {
        return new SymMonoid<Set<T>>() {
            @Override
            public Set<T> apply(Set<T> l, Set<T> r) {
                if (l instanceof PSet) {
                    return ((PSet<T>) l).plusAll(r);
                } else if (r instanceof PSet) {
                    return ((PSet<T>) r).plusAll(l);
                }

                return HashTreePSet.from(l).plusAll(r);
            }

            @Override
            public Set<T> lift(Set<T> set) {
                return HashTreePSet.from(set);
            }

            @Override
            public Set<T> zero() {
                return HashTreePSet.empty();
            }
        };
    }

    @NonNull
    private static <T> SymMonoid<Set<T>> mutableSetMonoid() {
        return new SymMonoid<Set<T>>() {
            @Override
            public Set<T> apply(Set<T> l, Set<T> r) {
                l.addAll(r);
                return l;
            }

            @Override
            public Set<T> lift(Set<T> set) {
                return new LinkedHashSet<>(set);
            }

            @Override
            public Set<T> zero() {
                return new LinkedHashSet<>();
            }
        };
    }

}
