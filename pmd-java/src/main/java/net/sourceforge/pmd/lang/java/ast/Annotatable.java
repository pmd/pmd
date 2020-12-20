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
     * Returns a specific annotation on this node, or null if absent.
     *
     * @param binaryName
     *            Binary name of the annotation type.
     *            Note: for now, canonical names are tolerated, this may be changed in PMD 7.
     */
    ASTAnnotation getAnnotation(String binaryName);

    /**
     * Checks whether any annotation is present on this node.
     *
     * @param binaryNames
     *            Collection that contains binary names of annotations.
     *            Note: for now, canonical names are tolerated, this may be changed in PMD 7.
     * @return <code>true</code> if any annotation is present on this node, else <code>false</code>
     */
    boolean isAnyAnnotationPresent(Collection<String> binaryNames);

    /**
     * Checks whether the annotation is present on this node.
     *
     * @param binaryName
     *            Binary name of the annotation type.
     *            Note: for now, canonical names are tolerated, this may be changed in PMD 7.
     * @return <code>true</code> if the annotation is present on this node, else <code>false</code>
     */
    boolean isAnnotationPresent(String binaryName);
}
