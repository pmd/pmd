/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

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

    /**
     * Returns the constant value of this node, if this is a constant
     * expression. Otherwise, or if some references couldn't be resolved,
     * returns null. Note that {@link ASTNullLiteral null} is not a constant
     * value, so this method's returning null is not a problem. Note that
     * annotations are not given a constant value by this implementation.
     */
    default @Nullable Object getConstValue() {
        return null;
    }
}
