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

    private final Map<String, UnresolvedClassImpl> store = new HashMap<>();

    /**
     * Produces an unresolved class symbol from the given canonical name.
     *
     * @param canonicalName Canonical name of the returned symbol
     *
     * @throws NullPointerException     If the name is null
     * @throws IllegalArgumentException If the name is empty
     */
    public @NonNull JClassSymbol makeUnresolvedReference(String canonicalName) {
        return store.computeIfAbsent(canonicalName, UnresolvedClassImpl::new);
    }

}
