/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.function.Predicate;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

/**
 * Marks nodes that can be annotated. {@linkplain ASTAnnotation Annotations}
 * are most often the first few children of the node they apply to.
 * E.g. in {@code @Positive int}, the {@code @Positive} annotation is
 * a child of the {@link ASTPrimitiveType PrimitiveType} node. This
 * contrasts with PMD 6.0 grammar, where the annotations were most often
 * the preceding siblings.
 */
public interface Annotatable extends JavaNode {

    /**
     * Returns all annotations present on this node.
     */
    default NodeStream<ASTAnnotation> getDeclaredAnnotations() {
        return children(ASTAnnotation.class);
    }


    /**
     * Returns true if an annotation with the given qualified name is
     * applied to this node.
     */
    default boolean isAnnotationPresent(String annotQualifiedName) {
        return getDeclaredAnnotations().any(t -> TypeHelper.isA(t, annotQualifiedName));
    }


    /**
     * Returns true if an annotation with the given type is
     * applied to this node.
     */
    default boolean isAnnotationPresent(Class<?> type) {
        return getDeclaredAnnotations().any((Predicate<TypeNode>) t -> TypeHelper.subclasses(t, type));
    }
}
