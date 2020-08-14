/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;


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
 *
 * </pre>
 *
 */
public interface ASTType extends TypeNode, Annotatable, LeftRecursiveNode {


    /**
     * For now this returns the name of the type with all the segments,
     * without annotations, array dimensions, or type parameters. Experimental
     * because we need to specify it, eg it would be more useful to have
     * a method return a qualified name with help of the symbol table.
     */
    @Experimental
    String getTypeImage();

    /**
     * Returns the number of array dimensions of this type.
     * This is 0 unless this node {@linkplain #isArrayType()}.
     */
    default int getArrayDepth() {
        return 0;
    }


    /**
     * Returns true if this is the "void" pseudo-type, ie an {@link ASTVoidType}.
     */
    default boolean isVoid() {
        return this instanceof ASTVoidType;
    }

    // TODO remove that, there's enough on JTypeMirror

    default boolean isPrimitiveType() {
        return this instanceof ASTPrimitiveType;
    }


    default boolean isArrayType() {
        return this instanceof ASTArrayType;
    }


    default boolean isClassOrInterfaceType() {
        return this instanceof ASTClassOrInterfaceType;
    }


}
