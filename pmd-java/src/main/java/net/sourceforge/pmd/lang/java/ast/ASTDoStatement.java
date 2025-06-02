/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a {@code do ... while} statement.
 *
 *
 * <pre class="grammar">
 *
 * DoStatement ::= "do" {@linkplain ASTStatement Statement} "while" "(" {@linkplain ASTExpression Expression} ")" ";"
 *
 * </pre>
 */
public final class ASTDoStatement extends AbstractStatement implements ASTLoopStatement {

    ASTDoStatement(int id) {
        super(id);
    }


    /**
     * Returns the node that represents the guard of this loop.
     * This may be any expression of type boolean.
     */
    @Override
    public ASTExpression getCondition() {
        return (ASTExpression) getChild(1);
    }


    /**
     * Returns the statement that will be run while the guard
     * evaluates to true.
     */
    @Override
    public ASTStatement getBody() {
        return (ASTStatement) getChild(0);
    }


    @Override
    public <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
