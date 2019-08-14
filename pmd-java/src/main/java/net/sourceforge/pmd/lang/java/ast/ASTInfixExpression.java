/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Objects;

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
public class ASTInfixExpression extends AbstractJavaExpr implements ASTExpression, JSingleChildNode<ASTExpression>, LeftRecursiveNode {

    private BinaryOp operator;

    ASTInfixExpression(int i) {
        super(i);
    }

    ASTInfixExpression(JavaParser p, int i) {
        super(p, i);
    }


    @Override
    public ASTExpression jjtGetChild(int index) {
        return (ASTExpression) super.jjtGetChild(index);
    }


    /** Returns the left-hand-side operand. */
    public ASTExpression getLhs() {
        return jjtGetChild(0);
    }


    /** Returns the right-hand-side operand. */
    public ASTExpression getRhs() {
        return jjtGetChild(1);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    @Override
    public void setImage(String image) {
        throw new UnsupportedOperationException();
    }

    void setOp(BinaryOp op) {
        this.operator = Objects.requireNonNull(op);
    }

    @Override
    public String getImage() {
        return operator.toString();
    }

    /** Returns the operator. */
    public BinaryOp getOperator() {
        return operator;
    }
}
