/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Objects;

import net.sourceforge.pmd.lang.java.ast.InternalInterfaces.AtLeastOneChild;
import net.sourceforge.pmd.lang.java.ast.InternalInterfaces.BinaryExpressionLike;

/**
 * Represents a binary infix expression. {@linkplain ASTAssignmentExpression Assignment expressions}
 * are not represented by this node, because they're right-associative.
 *
 * <p>This node is used to represent expressions of different precedences.
 * The {@linkplain BinaryOp operator} is used to differentiate those expressions.
 *
 * <pre class="grammar">
 *
 * InfixExpression ::= {@link ASTExpression Expression} {@link BinaryOp} {@link ASTExpression Expression}
 *
 * </pre>
 *
 * <p>Binary expressions are all left-associative, and are parsed left-recursively.
 * For example, the expression {@code 1 * 2 * 3 % 4} parses as the following tree:
 *
 * <figure>
 * <img src="doc-files/binaryExpr_70x.svg" />
 * </figure>
 *
 * <p>In PMD 6.0.x, it would have parsed into the tree:
 *
 * <figure>
 * <img src="doc-files/binaryExpr_60x.svg" />
 * </figure>
 */
public final class ASTInfixExpression extends AbstractJavaExpr implements BinaryExpressionLike, AtLeastOneChild {

    private BinaryOp operator;

    ASTInfixExpression(int i) {
        super(i);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }



    void setOp(BinaryOp op) {
        this.operator = Objects.requireNonNull(op);
    }

    /**
     * Returns the right-hand side operand.
     *
     * <p>If this is an {@linkplain BinaryOp#INSTANCEOF instanceof expression},
     * then the right operand is a {@linkplain ASTTypeExpression TypeExpression}.
     */
    @Override
    public ASTExpression getRightOperand() {
        return BinaryExpressionLike.super.getRightOperand();
    }

    /** Returns the operator. */
    @Override
    public BinaryOp getOperator() {
        return operator;
    }

    // intentionally left-out

    @Override
    public void setImage(String image) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getImage() {
        return null;
    }

}
