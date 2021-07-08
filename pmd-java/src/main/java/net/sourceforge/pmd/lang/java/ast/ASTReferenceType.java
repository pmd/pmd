/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a reference type, i.e. a {@linkplain ASTClassOrInterfaceType class or interface type},
 * or an {@linkplain ASTArrayType array type}.
 *
 * <pre class="grammar">
 *
 *  ReferenceType ::= {@link ASTClassOrInterfaceType ClassOrInterfaceType}
 *                  | {@link ASTArrayType ArrayType}
 *                  | {@link ASTIntersectionType IntersectionType}
 *                  | {@link ASTUnionType UnionType}
 *                  | {@link ASTWildcardType WildcardType}
 *
 * </pre>
 */
public interface ASTReferenceType extends ASTType {
}
