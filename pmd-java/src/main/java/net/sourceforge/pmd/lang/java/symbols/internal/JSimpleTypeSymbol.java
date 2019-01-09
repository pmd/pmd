/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

/**
 * A reference type that can be referred to using a simple name.
 * These include references to class or interfaces (be they
 * {@linkplain JClassSymbol resolved} or only {@link JResolvableClassSymbol symbolic}),
 * and references to {@linkplain JTypeParameterSymbol type parameters},
 * but not array types or parameterized types. Primitive types are
 * excluded as well because that wouldn't be useful.
 *
 * This can probably be unified with types symbols (including array types,
 * intersection types, wildcard types) in a later stage to make type
 * resolution depend only on this abstract representation.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JSimpleTypeSymbol extends JElementSymbol {


    /**
     * Returns true if this is a resolved class reference, in
     * which case it can be safely downcast to {@link JClassSymbol}.
     */
    default boolean isResolvedClass() {
        return this instanceof JClassSymbol;
    }


    /**
     * Returns true if this is a class reference, in
     * which case it can be safely downcast to {@link JResolvableClassSymbol}.
     */
    default boolean isUnresolvedClass() {
        return this instanceof JResolvableClassSymbol;
    }


    /**
     * Returns true if this is a reference to a type variable, in
     * which case it can be safely downcast to {@link JTypeParameterSymbol}.
     */
    default boolean isTypeVariable() {
        return this instanceof JTypeParameterSymbol;
    }
}
