/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A non-fallthrough switch case, written with an arrow ({@code ->})
 * instead of a colon.
 *
 * <pre class="grammar">
 *
 * SwitchLabeledRule ::= {@link ASTSwitchLabeledExpression SwitchLabeledExpression}
 *                     | {@link ASTSwitchLabeledBlock SwitchLabeledBlock}
 *                     | {@link ASTSwitchLabeledThrowStatement SwitchLabeledThrowStatement}
 *
 * </pre>
 */
public interface ASTSwitchLabeledRule extends JavaNode, LeftRecursiveNode {
    // needs to extend LeftRecursiveNode to fit the text range to the children

    /**
     * Returns the label of this expression.
     */
    default ASTSwitchLabel getLabel() {
        return (ASTSwitchLabel) jjtGetChild(0);
    }

}
