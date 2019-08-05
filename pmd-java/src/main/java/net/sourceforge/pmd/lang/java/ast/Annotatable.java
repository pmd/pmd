/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collection;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

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
    default List<ASTAnnotation> getDeclaredAnnotations() {
        return this.findChildrenOfType(ASTAnnotation.class);
    }


    /**
     * Returns the annotation with the given qualified name if it is present,
     * otherwise returns null. The argument should be a qualified name, though
     * this method will find also usages of an annotation that use the simple
     * name if it is in scope.
     *
     * <p>E.g. {@code getAnnotation("java.lang.Override")} will find both
     * {@code @java.lang.Override} and {@code @Override}.
     */
    @Nullable
    default ASTAnnotation getAnnotation(String annotQualifiedName) {
        // TODO use node streams
        List<ASTAnnotation> annotations = getDeclaredAnnotations();
        for (ASTAnnotation annotation : annotations) {
            if (TypeHelper.isA(annotation, annotQualifiedName)) {
                return annotation;
            }
        }
        return null;
    }


    /**
     * Returns true if any annotation in the given collection is present,
     * using {@link #isAnnotationPresent(String)}, otherwise false.
     */
    default boolean isAnyAnnotationPresent(Collection<String> annotQualifiedNames) {
        // TODO use node streams
        for (String annotQualifiedName : annotQualifiedNames) {
            if (isAnnotationPresent(annotQualifiedName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns true if an annotation with the given qualified name is
     * applied to this node. In this case, {@link #getAnnotation(String)}
     * will not return null.
     */
    default boolean isAnnotationPresent(String annotQualifiedName) {
        return getAnnotation(annotQualifiedName) != null;
    }
}
