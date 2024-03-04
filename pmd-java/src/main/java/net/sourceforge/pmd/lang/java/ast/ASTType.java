/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;


/**
 * Represents a type reference.
 *
 * <p>Corresponds to the JLS's <a href="https://docs.oracle.com/javase/specs/jls/se11/html/jls-4.html#jls-Type">Type</a>
 * and <a href="https://docs.oracle.com/javase/specs/jls/se10/html/jls-8.html#jls-UnannType">UnannType</a>
 * at the same time. In some contexts this can also be an {@linkplain ASTIntersectionType intersection type},
 * though the JLS has no production for that.
 *
 * <pre class="grammar">
 *
 * Type ::= {@link ASTReferenceType ReferenceType}
 *        | {@link ASTPrimitiveType PrimitiveType}
 *        | {@link ASTVoidType VoidType}
 *
 * </pre>
 *
 */
public interface ASTType extends TypeNode, Annotatable, LeftRecursiveNode {

    // these are NoAttribute, so they don't end up in the tree dump tests

    /**
     * Returns true if this is the "void" pseudo-type, ie an {@link ASTVoidType}.
     */
    @NoAttribute
    default boolean isVoid() {
        return this instanceof ASTVoidType;
    }
}
