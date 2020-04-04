/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;

enum SuperTypesEnumerator {
    JUST_SELF {
        @Override
        public Iterator<JClassSymbol> iterator(JClassSymbol t) {
            return IteratorUtil.singletonIterator(t);
        }
    },
    ALL_STRICT_SUPERTYPES {
        @Override
        public Iterator<JClassSymbol> iterator(JClassSymbol t) {
            Iterator<JClassSymbol> iter = ALL_SUPERTYPES_INCLUDING_SELF.iterator(t);
            IteratorUtil.advance(iter, 1);
            return iter;
        }
    },
    ALL_SUPERTYPES_INCLUDING_SELF {
        @Override
        public Iterator<JClassSymbol> iterator(JClassSymbol t) {
            Iterator<JClassSymbol> selfAndSuperClasses = IteratorUtil.generate(t, JClassSymbol::getSuperclass);

            return flatMapToAddInterfaces(selfAndSuperClasses);
        }

        @NonNull
        private Iterator<JClassSymbol> flatMapToAddInterfaces(Iterator<JClassSymbol> classes) {

            return new Iterator<JClassSymbol>() {
                private final Set<JClassSymbol> seenInterfaces = new HashSet<>();
                private Iterator<JClassSymbol> interfaces = Collections.emptyIterator();

                @Override
                public boolean hasNext() {
                    return interfaces.hasNext() || classes.hasNext();
                }

                @Override
                public JClassSymbol next() {
                    if (interfaces.hasNext()) {
                        final JClassSymbol nextInterface = interfaces.next();
                        seenInterfaces.add(nextInterface);
                        return nextInterface;
                    }
                    final JClassSymbol nextSuperclass = classes.next();
                    final Set<JClassSymbol> currentInterfaces = new LinkedHashSet<>();
                    walkInterfaces(currentInterfaces, nextSuperclass);
                    interfaces = currentInterfaces.iterator();
                    return nextSuperclass;
                }

                private void walkInterfaces(final Set<JClassSymbol> addTo, final JClassSymbol c) {
                    for (final JClassSymbol iface : c.getSuperInterfaces()) {
                        if (!seenInterfaces.contains(iface)) {
                            addTo.add(iface);
                        }
                        walkInterfaces(addTo, iface);
                    }
                }
            };
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
