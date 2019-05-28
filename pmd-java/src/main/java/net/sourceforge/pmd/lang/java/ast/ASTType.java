/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;


/**
 * Represents a type reference.
 *
 * <p>Corresponds to the JLS's <a href="https://docs.oracle.com/javase/specs/jls/se11/html/jls-4.html#jls-Type">Type</a>.
 *
 * <pre class="grammar">
 *
 * Type ::= {@link ASTReferenceType ReferenceType}
 *        | {@link ASTPrimitiveType PrimitiveType}
 *
 * </pre>
 *
 * Note: it is not exactly the same the "UnannType" defined in JLS.
 *
 */
public interface ASTType extends TypeNode, Annotatable {

    /**
     * Returns all annotations present on this node.
     */
    @Override
    default List<ASTAnnotation> getDeclaredAnnotations() {
        // TODO use node streams
        ASTAnnotationList lst = getFirstChildOfType(ASTAnnotationList.class);
        return lst == null ? Collections.emptyList() : lst.findChildrenOfType(ASTAnnotation.class);
    }


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

    @Nullable
    default ASTPrimitiveType asPrimitiveType() {
        return isPrimitiveType() ? (ASTPrimitiveType) this : null;
    }

    @Nullable
    default ASTReferenceType asReferenceType() {
        return isReferenceType() ? (ASTReferenceType) this : null;
    }


    default boolean isPrimitiveType() {
        return this instanceof ASTPrimitiveType;
    }


    default boolean isReferenceType() {
        return !isPrimitiveType();
    }


    default boolean isArrayType() {
        return this instanceof ASTArrayType;
    }


    default boolean isClassOrInterfaceType() {
        return this instanceof ASTClassOrInterfaceType;
    }


}
