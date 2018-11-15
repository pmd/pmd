/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * A reference type that can be referred to using a simple name.
 * These include references to {@linkplain JSymbolicClassReference class or interfaces}
 * and references to {@linkplain JTypeVariableReference type parameters},
 * but not array types or parameterized types. Primitive types are excluded
 * as well because that wouldn't be useful.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JSimpleTypeReference<N extends Node> extends JCodeReference<N> {


    /**
     * Returns true if this is a class reference, in
     * which case it can be safely downcast to {@link JSymbolicClassReference}.
     */
    default boolean isSymbolicClass() {
        return this instanceof JSymbolicClassReference;
    }


    /**
     * Returns true if this is a reference to a type variable, in
     * which case it can be safely downcast to {@link JTypeVariableReference}.
     */
    default boolean isTypeVariable() {
        return this instanceof JTypeVariableReference;
    }
}
