/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents an increment or decrement operation on a variable. This represents
 * both the postfix and prefix forms.
 *
 * <p>Since 7.0, this node replaces the nodes PostfixExpression, PreDecrementExpression,
 * and PreIncrementExpression.
 *
 * <p>In prefix form, this has the same precedence as {@linkplain ASTUnaryExpression UnaryExpression}.
 * In postfix form, this has a precedence greater as {@linkplain ASTUnaryExpression UnaryExpression},
 * and lower as {@linkplain ASTPrimaryExpression PrimaryExpression}.
 *
 * <pre class="grammar">
 *
 * IncrementExpr ::= {@linkplain ASTAssignableExpr AssignableExpr} ( "++" | "--" )
 *                 | ( "++" | "--" ) {@linkplain ASTAssignableExpr AssignableExpr}
 *
 * </pre>
 */
public final class ASTIncrementExpression extends AbstractJavaExpr implements ASTExpression, LeftRecursiveNode {

    private boolean isPrefix;
    private boolean isIncrement;

    ASTIncrementExpression(int id) {
        super(id);
    }

    ASTIncrementExpression(JavaParser p, int id) {
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


    void setPrefix() {
        this.isPrefix = true;
    }


    void setIncrement() {
        this.isIncrement = true;
    }

    /**
     * Returns true if this a prefix assignment, {@literal e.g.} {@code ++i}, {@code --i}.
     */
    public boolean isPrefix() {
        return isPrefix;
    }

    /**
     * Returns true if this a postfix assignment, {@literal e.g.} {@code i++}, {@code i--}.
     */
    public boolean isPostfix() {
        return !isPrefix;
    }

    /**
     * Returns the operator of this expression.
     */
    public IncrementOp getOp() {
        return isIncrement ? IncrementOp.INCREMENT : IncrementOp.DECREMENT;
    }

    /**
     * Returns the name of the operator.
     */
    public String getOpName() {
        return getOp().name();
    }

    /**
     * Returns true if this is an increment expression ({@link #getOp()} is {@link IncrementOp#INCREMENT}).
     */
    public boolean isIncrement() {
        return isIncrement;
    }

    /**
     * Returns true if this is a decrement expression ({@link #getOp()} is {@link IncrementOp#DECREMENT}).
     */
    public boolean isDecrement() {
        return !isIncrement;
    }


    /**
     * Returns the expression assigned by this expression.
     */
    public ASTAssignableExpr getBaseExpression() {
        return (ASTAssignableExpr) jjtGetChild(0);
    }

}
