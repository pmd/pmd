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

    ASTSynchronizedStatement(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }

    /**
     * Returns the expression evaluating to the lock object.
     */
    public ASTExpression getLockExpression() {
        return (ASTExpression) jjtGetChild(0);
    }

    /**
     * Returns the body of the statement.
     */
    public ASTBlock getBody() {
        return (ASTBlock) jjtGetChild(1);
    }
}
