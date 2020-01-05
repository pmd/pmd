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
public final class ASTDoStatement extends AbstractJavaNode {

    ASTDoStatement(int id) {
        super(id);
    }

    ASTDoStatement(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * Returns the node that represents the guard of this loop.
     * This may be any expression of type boolean.
     *
     * @deprecated Use {@link #getCondition()}
     */
    @Deprecated
    public ASTExpression getGuardExpressionNode() {
        return getCondition();
    }

    /**
     * Returns the node that represents the guard of this loop.
     * This may be any expression of type boolean.
     */
    public ASTExpression getCondition() {
        return (ASTExpression) jjtGetChild(1);
    }


    /**
     * Returns the statement that will be run while the guard
     * evaluates to true.
     */
    public ASTStatement getBody() {
        return (ASTStatement) jjtGetChild(0);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }
}
