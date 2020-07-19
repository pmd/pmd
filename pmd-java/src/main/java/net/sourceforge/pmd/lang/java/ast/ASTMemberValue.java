/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents the value of a member of an annotation.
 * This can appear in a {@linkplain ASTMemberValuePair member-value pair},
 * or in the {@linkplain ASTDefaultValue default clause} of an annotation
 * method.
 *
 * <pre class="grammar">
 *
 * MemberValue ::= {@link ASTAnnotation Annotation}
 *               | {@link ASTMemberValueArrayInitializer MemberValueArrayInitializer}
 *               | {@link ASTExpression &lt; any constant expression &gt;}
 *
 * </pre>
 */
public interface ASTMemberValue extends JavaNode {
}
