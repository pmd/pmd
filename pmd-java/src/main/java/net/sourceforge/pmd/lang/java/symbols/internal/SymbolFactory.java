/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;


import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 * Builds symbols. This is owned by a {@link TypeSystem}, which is
 * tasked with creating it itself. This is internal API.
 */
@InternalApi
public final class SymbolFactory {

    private final TypeSystem ts;

    /**
     * Constructor for a new root type system. This may only be called
     * from the constructor of {@link TypeSystem} (throws an {@link AssertionError}
     * otherwise), and is internal.
     */
    public SymbolFactory(TypeSystem typeSystem) {
        this.ts = typeSystem;
    }

    /**
     * Returns the type system this factory is bound to.
     */
    public TypeSystem getTypeSystem() {
        return ts;
    }

    /**
     * Fake symbols are used to give a symbol to array components that
     * cannot be represented by a symbol. This includes inference variables.
     */
    public JTypeDeclSymbol fakeSymbol(String name) {
        return new FakeTypeSymbol(this, name);
    }


    /**
     * Produces a new unresolved class symbol from the given canonical name.
     *
     * @param canonicalName Canonical name of the returned symbol
     * @param typeArity     Number of type arguments parameterizing the reference.
     *                      Type parameter symbols will be created to represent them.
     *
     * @throws NullPointerException If the name is null
     * @throws AssertionError       If the name is not a canonical name
     */
    public @NonNull JClassSymbol makeUnresolvedReference(String canonicalName, int typeArity) {
        FlexibleUnresolvedClassImpl sym = new FlexibleUnresolvedClassImpl(this, null, canonicalName);
        sym.setTypeParameterCount(typeArity);
        return sym;
    }

}
