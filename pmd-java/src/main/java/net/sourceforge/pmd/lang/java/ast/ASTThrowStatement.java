/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A {@code throw} statement.
 *
 * <pre class="grammar">
 *
 * ThrowStatement ::= "throw" {@link ASTExpression Expression} ";"
 *
 * </pre>
 */
public final class ASTThrowStatement extends AbstractStatement implements ASTSwitchArrowRHS {

    ASTThrowStatement(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the expression for the thrown exception.
     */
    public ASTExpression getExpr() {
        return (ASTExpression) getFirstChild();
    }

}
