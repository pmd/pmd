/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl;


import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;

/**
 * Builds symbols.
 *
 * <p>This may be improved later to eg cache and reuse the most recently
 * accessed symbols (there may be a lot of cache hits in a typical java file).
 *
 * @param <T> Type of stuff this factory can convert to symbols. We'll
 *            implement it for {@link Class} and {@link ASTAnyTypeDeclaration}.
 */
public interface SymbolFactory<T> {

    /**
     * Produces an array symbol from the given component symbol (one dimension).
     * The component can naturally be another array symbol, but cannot be an
     * anonymous class.
     *
     * @param component Component symbol of the array
     *
     * @throws NullPointerException     If the component is null
     * @throws IllegalArgumentException If the component is the symbol for an anonymous class
     */
    @NonNull
    default JClassSymbol makeArraySymbol(JTypeDeclSymbol component) {
        return new ArraySymbolImpl(this, component);
    }


    /**
     * Returns the symbol representing the given class. Returns null if
     * the given class is itself null.
     *
     * @param klass Object representing a class
     */
    JClassSymbol getClassSymbol(@Nullable T klass);


}
