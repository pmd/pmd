/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents an annotation. This node has three specific syntactic variants,
 * represented by nodes that implement this interface.
 *
 * <pre class="grammar">
 *
 * Annotation ::= {@linkplain ASTNormalAnnotation NormalAnnotation}
 *              | {@linkplain ASTSingleMemberAnnotation SingleMemberAnnotation}
 *              | {@linkplain ASTMarkerAnnotation MarkerAnnotation}
 *
 * </pre>
 */
public interface ASTAnnotation extends TypeNode, ASTMemberValue {


    /**
     * Returns the name of the annotation as it is used,
     * eg {@code java.lang.Override} or {@code Override}.
     */
    default String getAnnotationName() {
        return getImage();
    }


    /**
     * Returns the simple name of the annotation.
     */
    default String getSimpleName() {
        String[] split = getImage().split("\\.");
        return split[split.length - 1];
    }


}

