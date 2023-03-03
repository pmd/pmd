/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;

/**
 * A symbol resolver that knows about a few hand-picked symbols.
 */
final class MapSymResolver implements SymbolResolver {

    private final Map<String, JClassSymbol> byCanonicalName;
    private final Map<String, JClassSymbol> byBinaryName;

    MapSymResolver(Map<String, JClassSymbol> byCanonicalName,
                   Map<String, JClassSymbol> byBinaryName) {
        this.byCanonicalName = byCanonicalName;
        this.byBinaryName = byBinaryName;
    }

    @Override
    public @Nullable JClassSymbol resolveClassFromBinaryName(@NonNull String binaryName) {
        return byBinaryName.get(binaryName);
    }

    @Override
    public @Nullable JClassSymbol resolveClassFromCanonicalName(@NonNull String canonicalName) {
        return byCanonicalName.get(canonicalName);
    }
}
