/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import static java.util.Collections.emptySet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;

/**
 * Strategies to enumerate a type hierarchy.
 */
enum SuperTypesEnumerator {
    JUST_SELF {
        @Override
        public Iterator<JClassSymbol> iterator(JClassSymbol t) {
            return IteratorUtil.singletonIterator(t);
        }
    },

    /**
     * Superclasses of t, including t, in innermost to outermost. If t
     * is an interface, contains just t.
     */
    SUPERCLASSES_AND_SELF {
        @Override
        public Iterator<JClassSymbol> iterator(JClassSymbol t) {
            return IteratorUtil.generate(t, JClassSymbol::getSuperclass);
        }
    },

    /**
     * All direct strict supertypes, starting with the superclass if
     * it exists.
     */
    DIRECT_STRICT_SUPERTYPES {
        @Override
        public Iterator<JClassSymbol> iterator(JClassSymbol t) {
            return iterable(t).iterator();
        }

        @Override
        public Iterable<JClassSymbol> iterable(JClassSymbol t) {
            @Nullable JClassSymbol sup = t.getSuperclass();
            List<JClassSymbol> superItfs = t.getSuperInterfaces();

            LinkedHashSet<JClassSymbol> set;
            if (sup != null) {
                set = new LinkedHashSet<>(superItfs.size() + 1);
                set.add(sup);
            } else {
                if (superItfs.size() == 0) {
                    return emptySet();
                }
                set = new LinkedHashSet<>(superItfs.size());
            }

            set.addAll(superItfs);

            return set;
        }
    },


    /**
     * Restriction of {@link #ALL_SUPERTYPES_INCLUDING_SELF} to just the
     * strict supertypes.
     */
    ALL_STRICT_SUPERTYPES {
        @Override
        public Iterator<JClassSymbol> iterator(JClassSymbol t) {
            Iterator<JClassSymbol> iter = ALL_SUPERTYPES_INCLUDING_SELF.iterator(t);
            IteratorUtil.advance(iter, 1);
            return iter;
        }
    },

    /**
     * Walks supertypes depth-first, without duplicates. For example
     * for the following:
     * <pre>{@code
     *
     * interface I1 { } // yields I1
     *
     * interface I2 extends I1 { }  // yields I2, I1
     *
     * class Sup implements I2 { }  // yields Sup, I2, I1
     *
     * class Sub extends Sup implements I1 { } // yields Sub, I1, Sup, I2
     *
     * }</pre>
     *
     * iterating from {@code Sub} will yield {@code Sub, I1, Sup, I2}
     */
    ALL_SUPERTYPES_INCLUDING_SELF {
        @Override
        public Iterator<JClassSymbol> iterator(JClassSymbol t) {
            final Set<JClassSymbol> seenInterfaces = new HashSet<>();
            return IteratorUtil.flatMapWithSelf(SUPERCLASSES_AND_SELF.iterator(t), type -> {
                final Set<JClassSymbol> currentInterfaces = new LinkedHashSet<>();
                walkInterfaces(seenInterfaces, currentInterfaces, type);
                return currentInterfaces.iterator();
            });
        }

        private void walkInterfaces(final Set<JClassSymbol> seen, final Set<JClassSymbol> addTo, final JClassSymbol c) {
            for (final JClassSymbol iface : c.getSuperInterfaces()) {
                if (seen.add(iface)) {
                    addTo.add(iface);
                    walkInterfaces(seen, addTo, iface); // Recurses into all super itfs
                }
            }
        }
    };


    public abstract Iterator<JClassSymbol> iterator(JClassSymbol t);

    public Stream<JClassSymbol> stream(JClassSymbol t) {
        return StreamSupport.stream(iterable(t).spliterator(), false);

    }

    public Iterable<JClassSymbol> iterable(JClassSymbol t) {
        return () -> iterator(t);
    }
}
