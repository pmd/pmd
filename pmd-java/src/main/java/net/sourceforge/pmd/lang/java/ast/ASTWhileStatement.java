/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a {@code while} loop.
 *
 * <pre class="grammar">
 *
 * WhileStatement ::= "while" "(" {@linkplain ASTExpression Expression} ")" {@linkplain ASTStatement Statement}
 *
 * </pre>
 */
public final class ASTWhileStatement extends AbstractStatement implements ASTLoopStatement {

    ASTWhileStatement(int id) {
        super(id);
    }


    /**
     * Returns the node that represents the guard of this loop.
     * This may be any expression of type boolean.
     */
    public ASTExpression getCondition() {
        return (ASTExpression) getChild(0);
    }



    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
