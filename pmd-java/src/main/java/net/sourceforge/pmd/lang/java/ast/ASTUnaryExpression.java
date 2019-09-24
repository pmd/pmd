/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import java.util.Objects;

/**
 * Represents a unary prefix operation on a value.
 * This has a precedence greater than {@link ASTMultiplicativeExpression}.
 *
 * <p>UnaryExpression has the same precedence as the prefix forms of {@linkplain ASTIncrementExpression IncrementExpression},
 * and {@linkplain ASTCastExpression CastExpression}.
 *
 * <p>Note that the child of this node is not necessarily a UnaryExpression,
 * rather, it can be an expression with an operator precedence greater or equal
 * to a UnaryExpression.
 *
 * <pre class="grammar">
 *
 * UnaryExpression ::= {@link UnaryOp} UnaryExpression
 *
 * </pre>
 */
public final class ASTUnaryExpression extends AbstractJavaExpr implements ASTExpression {

    private UnaryOp operator;

    ASTUnaryExpression(int id) {
        super(id);
    }

    ASTUnaryExpression(JavaParser p, int id) {
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


    public ASTExpression getOperand() {
        return (ASTExpression) jjtGetChild(0);
    }

    @Override
    public void setImage(String image) {
        super.setImage(image);
        this.operator = Objects.requireNonNull(UnaryOp.fromImage(image));
    }


    /**
     * Returns the constant representing the operator of this expression.
     */
    public UnaryOp getOperator() {
        return operator;
    }
}
