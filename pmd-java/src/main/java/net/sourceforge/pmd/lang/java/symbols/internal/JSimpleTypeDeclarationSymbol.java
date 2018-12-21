/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * A reference type that can be referred to using a simple name.
 * These include references to {@linkplain JResolvableClassDeclarationSymbol class or interfaces}
 * and references to {@linkplain JTypeParameterSymbol type parameters},
 * but not array types or parameterized types. Primitive types are excluded
 * as well because that wouldn't be useful.
 *
 * @param <N> Type of AST node that can represent this type of declaration
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JSimpleTypeDeclarationSymbol<N extends Node> extends JDeclarationSymbol<N> {

    /**
     * Returns true if this is a class reference, in
     * which case it can be safely downcast to {@link JResolvableClassDeclarationSymbol}.
     */
    default boolean isSymbolicClass() {
        return this instanceof JResolvableClassDeclarationSymbol;
    }


    /**
     * Returns true if this is a reference to a type variable, in
     * which case it can be safely downcast to {@link JTypeParameterSymbol}.
     */
    default boolean isTypeVariable() {
        return this instanceof JTypeParameterSymbol;
    }
}
