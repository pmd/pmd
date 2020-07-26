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
import net.sourceforge.pmd.lang.java.types.JClassType;

/**
 * Strategies to enumerate a type hierarchy.
 */
enum SuperTypesEnumerator {
    JUST_SELF {
        @Override
        public Iterator<JClassType> iterator(JClassType t) {
            return IteratorUtil.singletonIterator(t);
        }
    },

    /**
     * Superclasses of t, including t, in innermost to outermost. If t
     * is an interface, contains t, and Object.
     */
    SUPERCLASSES_AND_SELF {
        @Override
        public Iterator<JClassType> iterator(JClassType t) {
            return IteratorUtil.generate(t, JClassType::getSuperClass);
        }
    },

    /**
     * All direct strict supertypes, starting with the superclass if
     * it exists. This includes Object if the search starts on an interface.
     */
    DIRECT_STRICT_SUPERTYPES {
        @Override
        public Iterator<JClassType> iterator(JClassType t) {
            return iterable(t).iterator();
        }

        @Override
        public Iterable<JClassType> iterable(JClassType t) {
            @Nullable JClassType sup = t.getSuperClass();
            List<JClassType> superItfs = t.getSuperInterfaces();

            LinkedHashSet<JClassType> set;
            if (sup != null) {
                set = new LinkedHashSet<>(superItfs.size() + 1);
                set.add(sup);
            } else {
                if (superItfs.isEmpty()) {
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
     * strict supertypes. This includes Object if the search starts
     * on an interface.
     */
    ALL_STRICT_SUPERTYPES {
        @Override
        public Iterator<JClassType> iterator(JClassType t) {
            Iterator<JClassType> iter = ALL_SUPERTYPES_INCLUDING_SELF.iterator(t);
            IteratorUtil.advance(iter, 1);
            return iter;
        }
    },

    /**
     * Walks supertypes depth-first, without duplicates. This includes
     * Object if the search starts on an interface. For example for the following:
     * <pre>{@code
     *
     * interface I1 { } // yields I1, Object
     *
     * interface I2 extends I1 { }  // yields I2, I1, Object
     *
     * class Sup implements I2 { }  // yields Sup, I2, I1, Object
     *
     * class Sub extends Sup implements I1 { } // yields Sub, I1, Sup, I2, Object
     *
     * }</pre>
     */
    ALL_SUPERTYPES_INCLUDING_SELF {
        @Override
        public Iterator<JClassType> iterator(JClassType t) {
            final Set<JClassType> seenInterfaces = new HashSet<>();
            return IteratorUtil.flatMapWithSelf(SUPERCLASSES_AND_SELF.iterator(t), type -> {
                final Set<JClassType> currentInterfaces = new LinkedHashSet<>();
                walkInterfaces(seenInterfaces, currentInterfaces, type);
                return currentInterfaces.iterator();
            });
        }

        private void walkInterfaces(final Set<JClassType> seen, final Set<JClassType> addTo, final JClassType c) {
            for (final JClassType iface : c.getSuperInterfaces()) {
                if (seen.add(iface)) {
                    addTo.add(iface);
                    walkInterfaces(seen, addTo, iface); // Recurses into all super itfs
                }
            }
        }
    };


    public abstract Iterator<JClassType> iterator(JClassType t);

    public Stream<JClassType> stream(JClassType t) {
        return StreamSupport.stream(iterable(t).spliterator(), false);

    }

    public Iterable<JClassType> iterable(JClassType t) {
        return () -> iterator(t);
    }
}
