/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;

/**
 * Keeps track of unresolved classes, can update the arity of unresolved
 * types if needed.
 *
 * <p>Not thread-safe.
 */
public final class UnresolvedClassStore {

    private final Map<String, UnresolvedClassImpl> unresolved = new HashMap<>();
    private final SymbolFactory symbols;

    public UnresolvedClassStore(SymbolFactory symbols) {
        this.symbols = symbols;
    }


    /**
     * Produces an unresolved class symbol from the given canonical name.
     *
     * @param canonicalName Canonical name of the returned symbol
     * @param typeArity     Number of type arguments parameterizing the reference.
     *                      Type parameter symbols will be created to represent them.
     *                      This may also mutate an existing unresolved reference.
     *
     * @throws NullPointerException If the name is null
     */
    public @NonNull JClassSymbol makeUnresolvedReference(String canonicalName, int typeArity) {
        UnresolvedClassImpl unresolved = this.unresolved.computeIfAbsent(canonicalName, n -> new FlexibleUnresolvedClassImpl(this.symbols, null, n));
        unresolved.setTypeParameterCount(typeArity);
        return unresolved;
    }
}
