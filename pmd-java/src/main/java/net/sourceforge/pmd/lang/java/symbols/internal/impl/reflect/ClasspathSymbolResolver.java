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
    public @Nullable JClassSymbol resolveClassFromBinaryName(@NonNull String canonicalName) {
        try {
            return factory.getClassSymbol(classLoader.loadClass(canonicalName));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public JClassSymbol resolveClassFromCanonicalName(@NonNull String canonicalName) {
        JClassSymbol symbol = resolveClassFromBinaryName(canonicalName);
        if (symbol != null) {
            return symbol;
        }
        int lastDotIdx = canonicalName.lastIndexOf('.');
        if (lastDotIdx < 0) {
            return null;
        } else {
            JClassSymbol outer = resolveClassFromCanonicalName(canonicalName.substring(0, lastDotIdx));
            if (outer != null) {
                String innerName = canonicalName.substring(lastDotIdx + 1);
                for (JClassSymbol inner : outer.getDeclaredClasses()) {
                    if (inner.getSimpleName().equals(innerName)) {
                        return inner;
                    }
                }
            }
        }

        return null;
    }

    @NonNull
    @Override
    public JClassSymbol resolveClassOrDefault(@NonNull String canonicalName) {
        JClassSymbol symbol = resolveClassFromCanonicalName(canonicalName);
        return symbol != null ? symbol : factory.makeUnresolvedReference(canonicalName);
    }
}
