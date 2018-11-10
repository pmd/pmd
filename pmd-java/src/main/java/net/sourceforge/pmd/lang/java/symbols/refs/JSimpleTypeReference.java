/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * A reference type that can be described using a simple name.
 * These include references to {@linkplain JSymbolicClassReference class or interfaces}
 * and references to {@linkplain JTypeVariableReference type parameters},
 * but not array types or parameterized types. Primitive types are excluded
 * as well because that wouldn't be useful.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JSimpleTypeReference<N extends Node> extends JCodeReference<N> {
}
