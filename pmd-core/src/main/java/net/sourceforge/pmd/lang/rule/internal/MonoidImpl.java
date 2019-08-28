/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@SuppressWarnings("unchecked")
final class MonoidImpl {

    // TODO using persistent collections would be beneficial.

    static final Monoid<? extends List> LIST_MONOID = new Monoid<List>() {
        @Override
        public List apply(List l, List r) {
            if (l.isEmpty()) {
                return r;
            } else if (r.isEmpty()) {
                return l;
            }
            ArrayList more = new ArrayList(l.size() + r.size());
            more.addAll(l);
            more.addAll(r);
            return more;
        }

        @Override
        public List zero() {
            return Collections.emptyList();
        }
    };
    static final Monoid<? extends Set> SET_MONOID = new Monoid<Set>() {
        @Override
        public Set apply(Set l, Set r) {
            if (l.isEmpty()) {
                return r;
            } else if (r.isEmpty()) {
                return l;
            }
            Set more = new LinkedHashSet(l);
            more.addAll(r);
            return more;
        }

        @Override
        public Set zero() {
            return Collections.emptySet();
        }
    };

}
