/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A branch of a {@link ASTSwitchLike SwitchLike}.
 *
 * <pre class="grammar">
 *
 * SwitchBranch ::= {@link ASTSwitchArrowBranch SwitchArrowBranch}
 *                | {@link ASTSwitchFallthroughBranch FallthroughBranch}
 *
 * </pre>
 */
public interface ASTSwitchBranch extends JavaNode {

    /**
     * Returns the label, which may be compound.
     */
    default ASTSwitchLabel getLabel() {
        return (ASTSwitchLabel) getFirstChild();
    }

    /** Return true if this is the default branch. */
    default boolean isDefault() {
        return getLabel().isDefault();
    }

}
