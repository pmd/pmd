/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

/**
 * The interface use to mark nodes that can be annotated.
 */
public interface Annotatable extends JavaNode {

    /**
     * Get all annotations present on this node.
     *
     * @return all annotations present on this node.
     */
    List<ASTAnnotation> getDeclaredAnnotations();


    @Nullable
    default ASTAnnotation getAnnotation(String annotQualifiedName) {
        List<ASTAnnotation> annotations = getDeclaredAnnotations();
        for (ASTAnnotation annotation : annotations) {
            if (TypeHelper.isA(annotation, annotQualifiedName)) {
                return annotation;
            }
        }
        return null;
    }


    default boolean isAnyAnnotationPresent(Collection<String> annotQualifiedNames) {
        for (String annotQualifiedName : annotQualifiedNames) {
            if (isAnnotationPresent(annotQualifiedName)) {
                return true;
            }
        }
        return false;
    }


    default boolean isAnnotationPresent(String annotQualifiedName) {
        return getAnnotation(annotQualifiedName) != null;
    }
}
