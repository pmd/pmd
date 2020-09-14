/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A non-fallthrough switch rule, introduced with switch expressions.
 * See {@link ASTSwitchLike}.
 *
 * <pre class="grammar">
 *
 * SwitchLabeledExpression ::= {@link ASTSwitchLabel SwitchLabel} "-&gt;" {@link ASTSwitchArrowRHS SwitchArrowRHS}
 *
 * </pre>
 */
public final class ASTSwitchArrowBranch extends AbstractJavaNode implements ASTSwitchBranch {

    ASTSwitchArrowBranch(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /** Returns the right hand side of the arrow. */
    public ASTSwitchArrowRHS getRightHandSide() {
        return (ASTSwitchArrowRHS) getLastChild();
    }
}
