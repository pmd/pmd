/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A node that can appear as the right-hand-side of a {@link ASTSwitchArrowBranch SwitchArrowRule}.
 *
 * <pre class="grammar">
 *
 * SwitchArrowRightHandSide ::= {@link ASTExpression Expression}
 *                            | {@link ASTBlock Block}
 *                            | {@link ASTThrowStatement ThrowStatement}
 *
 * </pre>
 */
public interface ASTSwitchArrowRHS extends JavaNode {

}
