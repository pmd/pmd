/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collection;
import java.util.List;

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

    /**
     * Get specific annotaion on this node.
     *
     * @param annotQualifiedName
     *            qulified name of the annotation.
     * @return <code>ASTAnnotaion</code> node if the annotation is present on this node, else <code>null</code>
     */
    ASTAnnotation getAnnotation(String annotQualifiedName);

    /**
     * Checks whether any annotation is present on this node.
     *
     * @param annotQualifiedNames
     *            collection that cotains qulified name of annotations.
     * @return <code>true</code> if any annotation is present on this node, else <code>false</code>
     */
    boolean isAnyAnnotationPresent(Collection<String> annotQualifiedNames);

    /**
     * Checks whether the annotation is present on this node.
     *
     * @param annotQualifiedName
     *            qulified name of the annotation.
     * @return <code>true</code> if the annotation is present on this node, else <code>false</code>
     */
    boolean isAnnotationPresent(String annotQualifiedName);
}
