/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.LinkedHashSet;
import java.util.Set;

import org.pcollections.HashTreePSet;
import org.pcollections.PSet;


@SuppressWarnings({"unchecked", "rawtypes"})
final class MonoidImplUtils {

    private MonoidImplUtils() {
        // utility class
    }

    static final IdMonoid PSET_MONOID = new IdMonoid<Set>() {
        @Override
        public Set apply(Set l, Set r) {
            if (l instanceof PSet) {
                return ((PSet) l).plusAll(r);
            } else if (r instanceof PSet) {
                return ((PSet) r).plusAll(l);
            }

            return HashTreePSet.from(l).plusAll(r);
        }

        @Override
        public Set zero() {
            return HashTreePSet.empty();
        }
    };

    static final IdMonoid MSET_MONOID = new IdMonoid<Set>() {
        @Override
        public Set apply(Set l, Set r) {
            l.addAll(r);
            return l;
        }

        @Override
        public Set zero() {
            return new LinkedHashSet();
        }
    };

}
