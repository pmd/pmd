/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collection;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

/**
 * The interface use to mark nodes that can be annotated.
 */
public interface Annotatable extends JavaNode {

    /**
     * Returns all annotations present on this node.
     */
    List<ASTAnnotation> getDeclaredAnnotations();


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
