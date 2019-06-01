/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents the value of a member of an annotation.
 * This can appear in a {@linkplain ASTMemberValuePair member-value pair},
 * or in a {@linkplain ASTSingleMemberAnnotation single-member annotation}.
 *
 * <pre class="grammar">
 *
 * MemberValue ::= {@link ASTAnnotation Annotation}
 *               | {@link ASTMemberValueArrayInitializer MemberValueArrayInitializer}
 *               | {@link ASTExpression &lt; any expression, excluding assignment expressions and lambda expressions &gt;}
 *
 * </pre>
 */
public interface ASTMemberValue extends JavaNode {

}
