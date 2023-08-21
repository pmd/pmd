/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents an {@code assert} statement.
 *
 * <pre class="grammar">
 *
 * AssertStatement ::= "assert" {@linkplain ASTExpression Expression} ( ":" {@linkplain ASTExpression Expression} )? ";"
 *
 * </pre>
 */
public final class ASTAssertStatement extends AbstractStatement {

    ASTAssertStatement(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the expression tested by this assert statement.
     */
    public ASTExpression getCondition() {
        return (ASTExpression) getChild(0);
    }


    /**
     * Returns true if this assert statement has a "detail message"
     * expression. In that case, {@link #getDetailMessageNode()} doesn't
     * return null.
     */
    public boolean hasDetailMessage() {
        return getNumChildren() == 2;
    }


    /**
     * Returns the expression that corresponds to the detail message,
     * i.e. the expression after the colon, if it's present.
     */
    public ASTExpression getDetailMessageNode() {
        return hasDetailMessage() ? (ASTExpression) getChild(1) : null;
    }

}
