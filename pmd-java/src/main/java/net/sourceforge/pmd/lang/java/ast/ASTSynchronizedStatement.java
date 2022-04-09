/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A synchronized statement.
 *
 * <pre class="grammar">
 *
 * SynchronizedStatement ::= "synchronized" "(" {@link ASTExpression Expression} ")" {@link ASTBlock Block}
 *
 * </pre>

 */
public final class ASTSynchronizedStatement extends AbstractStatement {

    ASTSynchronizedStatement(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the expression evaluating to the lock object.
     */
    public ASTExpression getLockExpression() {
        return (ASTExpression) getChild(0);
    }

    /**
     * Returns the body of the statement.
     */
    public ASTBlock getBody() {
        return (ASTBlock) getChild(1);
    }
}
