/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents an {@code if} statement, possibly with an {@code else} statement.
 *
 * <pre class="grammar">
 *
 * IfStatement ::= "if" "(" {@linkplain ASTExpression Expression} ")" {@linkplain ASTStatement Statement}
 *                 ( "else" {@linkplain ASTStatement Statement} )?
 *
 * </pre>
 */
public final class ASTIfStatement extends AbstractStatement {

    private boolean hasElse;


    ASTIfStatement(int id) {
        super(id);
    }


    void setHasElse() {
        this.hasElse = true;
    }


    /**
     * Returns true if this statement has an {@code else} clause.
     */
    public boolean hasElse() {
        return this.hasElse;
    }


    /**
     * Returns the node that represents the guard of this conditional.
     * This may be any expression of type boolean.
     */
    public ASTExpression getCondition() {
        return (ASTExpression) getChild(0);
    }


    /**
     * Returns the statement that will be run if the guard evaluates
     * to true.
     */
    public ASTStatement getThenBranch() {
        return (ASTStatement) getChild(1);
    }


    /**
     * Returns the statement of the {@code else} clause, if any.
     */
    public ASTStatement getElseBranch() {
        return hasElse() ? (ASTStatement) getChild(2) : null;
    }


    @Override
    public <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
