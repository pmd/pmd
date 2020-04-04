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

import org.apache.commons.lang3.ClassUtils.Interfaces;
import org.apache.commons.lang3.mutable.MutableObject;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;


class SymUtils {

    /**
     * Returns a stream of all supertypes of the given type.
     * The first element of the returned stream is the given type t,
     * so the stream is never empty.
     *
     * @param t                  Type to search
     * @param interfacesBehavior Whether to include interfaces or not
     */
    public static Stream<JClassSymbol> getSuperTypeStream(@NonNull JClassSymbol t, Interfaces interfacesBehavior) {
        return StreamSupport.stream(iterateSuperTypes(t, interfacesBehavior).spliterator(), false);
    }

    public static Iterable<JClassSymbol> iterateSuperTypes(@NonNull JClassSymbol t, Interfaces interfacesBehavior) {
        return () -> getSuperTypesIterator(t, interfacesBehavior);
    }

    public static Iterator<JClassSymbol> getSuperTypesIterator(@NonNull JClassSymbol t, Interfaces interfacesBehavior) {
        final MutableObject<JClassSymbol> next = new MutableObject<>(t);
        final Iterator<JClassSymbol> classes = new Iterator<JClassSymbol>() {

            @Override
            public boolean hasNext() {
                return next.getValue() != null;
            }

            @Override
            public JClassSymbol next() {
                final JClassSymbol result = next.getValue();
                next.setValue(result.getSuperclass());
                return result;
            }

        };

        if (interfacesBehavior != Interfaces.INCLUDE) {
            return classes;
        }

        final Set<JClassSymbol> seenInterfaces = new HashSet<>();

        return new Iterator<JClassSymbol>() {
            Iterator<JClassSymbol> interfaces = Collections.emptyIterator();

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

}
