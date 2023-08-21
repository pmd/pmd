/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


/**
 * Represents a unary operation on a value. The syntactic form may be
 * prefix or postfix, which are represented with the same nodes, even
 * though they have different precedences.
 *
 * <pre class="grammar">
 *
 * UnaryExpression ::= PrefixExpression | PostfixExpression
 *
 * PrefixExpression  ::= {@link UnaryOp PrefixOp} {@link ASTExpression Expression}
 *
 * PostfixExpression ::= {@link ASTExpression Expression} {@link UnaryOp PostfixOp}
 *
 * </pre>
 */
public final class ASTUnaryExpression extends AbstractJavaExpr {

    private UnaryOp operator;

    ASTUnaryExpression(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /** Returns the expression nested within this expression. */
    public ASTExpression getOperand() {
        return (ASTExpression) getChild(0);
    }


    /**
     * Returns true if this is a prefix expression.
     *
     * @deprecated XPath-attribute only, use {@code getOperator().isPrefix()} in java code.
     */
    @Deprecated
    public boolean isPrefix() {
        return getOperator().isPrefix();
    }

    /** Returns the constant representing the operator of this expression. */
    public UnaryOp getOperator() {
        return operator;
    }

    void setOp(UnaryOp op) {
        this.operator = op;
    }

}
