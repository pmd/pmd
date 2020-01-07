/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolFactory;

/**
 * Bridge into the internal API of this package.
 */
public final class ReflectSymInternals {

    private static final ReflectionSymFactory STATIC_FACTORY = new ReflectionSymFactory();

    private ReflectSymInternals() {
        // util class
    }

    /**
     * {@link SymbolFactory} cannot use {@link ReflectionSymFactory}
     * directly, because of class init cycle.
     */
    public static JClassSymbol createSharedSym(Class<?> klass) {
        return ReflectedClassImpl.createOuterClass(STATIC_FACTORY, klass);
    }

}
