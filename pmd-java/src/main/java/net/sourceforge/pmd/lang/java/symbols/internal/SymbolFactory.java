/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;


import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
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
     * Returns a symbol for the type induced by an intersection type.
     * Such a symbol is an interface if the superclass is {@link TypeSystem#OBJECT}.
     *
     * @param name       Name to give to the type
     * @param superClass Superclass of the new symbol
     * @param superItfs  Super interfaces of the new symbol
     *
     * @return A new symbol
     */
    public JClassSymbol fakeIntersectionSymbol(String name, JClassType superClass, List<JClassType> superItfs) {
        return new FakeIntersectionSymbol(name, superClass, superItfs);
    }


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
    public @NonNull JClassSymbol makeArraySymbol(JTypeDeclSymbol component) {
        return new ArraySymbolImpl(this, component);
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

    /**
     * Produces an unresolved class symbol from the given canonical name.
     *
     * @param container  Symbol declaring the field, if null, defaults to
     *                   {@link TypeSystem#UNRESOLVED_TYPE UNRESOLVED_TYPE.symbol()}
     * @param simpleName Simple name of the field
     *
     * @throws NullPointerException If the name is null
     * @throws AssertionError       If the name is not a java identifier
     */
    public @NonNull JFieldSymbol makeUnresolvedField(JClassSymbol container, String simpleName) {
        assert AssertionUtil.isJavaIdentifier(simpleName) : "Not a simple name '" + simpleName + "'";
        if (container == null) {
            container = (JClassSymbol) ts.UNRESOLVED_TYPE.getSymbol();
        }
        return new UnresolvedFieldSymbol(container, ts, simpleName);

    }
}
