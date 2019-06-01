/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.Rule;


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

    // @formatter:off
    /**
     * Returns true if this annotation suppresses the given rule.
     * The suppression annotation is {@link SuppressWarnings}.
     * This method returns true if this annotation is a SuppressWarnings,
     * and if the set of suppressed warnings ({@link SuppressWarnings#value()})
     * contains at least one of those:
     * <ul>
     *     <li>"PMD" (suppresses all rules);
     *     <li>"PMD.rulename", where rulename is the name of the given rule;
     *     <li>"all" (conventional value to suppress all warnings).
     * </ul>
     *
     * <p>Additionally, the following values suppress a specific set of rules:
     * <ul>
     *     <li>{@code "unused"}: suppresses rules like UnusedLocalVariable or UnusedPrivateField;
     *     <li>{@code "serial"}: suppresses BeanMembersShouldSerialize and MissingSerialVersionUID;
     * </ul>
     *
     * @param rule The rule for which to check for suppression
     *
     * @return True if this annotation suppresses the given rule
     */
    // @formatter:on
    default boolean suppresses(Rule rule) {
        return AstImplUtil.suppresses(this, rule);
    }
}

