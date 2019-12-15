/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;

/**
 * @author Clément Fournier
 */
public class ReflectSymInternals {

    public static final ReflectionSymFactory STATIC_FACTORY = new ReflectionSymFactory();

    public static JClassSymbol createSharedSym(Class<?> klass) {
        return ReflectedClassImpl.createOuterClass(STATIC_FACTORY, klass);
    }



}
