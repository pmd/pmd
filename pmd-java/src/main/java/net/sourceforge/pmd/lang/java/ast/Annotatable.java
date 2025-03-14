/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

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
     *
     * @param annotQualifiedName Note: for now, canonical names are tolerated, this may be changed in PMD 7.
     */
    default boolean isAnnotationPresent(String annotQualifiedName) {
        return getDeclaredAnnotations().any(t -> TypeTestUtil.isA(StringUtils.deleteWhitespace(annotQualifiedName), t));
    }


    /**
     * Returns true if an annotation with the given type is
     * applied to this node.
     */
    default boolean isAnnotationPresent(Class<?> type) {
        return getDeclaredAnnotations().any(t -> TypeTestUtil.isA(type, t));
    }


    /**
     * Returns a specific annotation on this node, or null if absent.
     *
     * @param binaryName
     *            Binary name of the annotation type.
     *            Note: for now, canonical names are tolerated, this may be changed in PMD 7.
     */
    default ASTAnnotation getAnnotation(String binaryName) {
        return getDeclaredAnnotations().filter(t -> TypeTestUtil.isA(StringUtils.deleteWhitespace(binaryName), t)).first();
    }

    /**
     * Checks whether any annotation is present on this node.
     *
     * @param binaryNames
     *            Collection that contains binary names of annotations.
     *            Note: for now, canonical names are tolerated, this may be changed in PMD 7.
     * @return <code>true</code> if any annotation is present on this node, else <code>false</code>
     */
    default boolean isAnyAnnotationPresent(Collection<String> binaryNames) {
        for (String annotQualifiedName : binaryNames) {
            if (isAnnotationPresent(annotQualifiedName)) {
                return true;
            }
        }
        return false;
    }
}
