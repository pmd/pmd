/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl;

import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;

/**
 * Makes and stores unresolved symbols (fallbacks).
 */
public class UnresolvedSymFactory {

    static final int UNKNOWN_ARITY = 0;

    private final Map<String, UnresolvedClassImpl> store = new HashMap<>();

    /**
     * Produces an unresolved class symbol from the given canonical name.
     *
     * @param canonicalName Canonical name of the returned symbol
     * @param typeArity     Number of type arguments parameterizing the reference.
     *                      Type parameter symbols will be created to represent them.
     *                      This may also mutate an existing unresolved reference.
     *
     * @throws NullPointerException     If the name is null
     * @throws IllegalArgumentException If the name is empty
     */
    public @NonNull JClassSymbol makeUnresolvedReference(String canonicalName, int typeArity) {
        UnresolvedClassImpl unresolved = store.computeIfAbsent(canonicalName, UnresolvedClassImpl::new);
        unresolved.setTypeParameterCount(typeArity);
        return unresolved;
    }

}
