/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a unary prefix operation on a value.
 * This has a precedence greater than {@link ASTMultiplicativeExpression}.
 *
 * <p>UnaryExpression has the same precedence as {@linkplain ASTPreIncrementExpression PreIncrementExpression},
 * {@linkplain ASTPreDecrementExpression PreDecrementExpression} and
 * {@linkplain ASTUnaryExpressionNotPlusMinus UnaryExpressionNotPlusMinus}.
 *
 * <p>Note that the child of this node is not necessarily a UnaryExpression,
 * rather, it can be an expression with an operator precedence greater or equal
 * to a UnaryExpression.
 *
 *
 * <pre>
 *
 * UnaryExpression ::= ( "+" | "-" ) UnaryExpression
 *
 * </pre>
 */
public class ASTUnaryExpression extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTUnaryExpression(int id) {
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
     * Returns the image of this unary operator, i.e. "+" or "-".
     */
    public String getOperator() {
        return getImage();
    }

}
