/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;


import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;

/**
 * Resolves symbols by asking a classloader.
 */
public class ClasspathSymbolResolver implements SymbolResolver {

    private final ClassLoader classLoader;
    private final ReflectionSymFactory factory;

    public ClasspathSymbolResolver(ClassLoader classLoader, ReflectionSymFactory factory) {
        this.classLoader = classLoader;
        this.factory = factory;
    }


    @Override
    public @Nullable JClassSymbol resolveClassFromBinaryName(@NonNull String binaryName) {
        try {
            return factory.getClassSymbol(classLoader.loadClass(binaryName));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
