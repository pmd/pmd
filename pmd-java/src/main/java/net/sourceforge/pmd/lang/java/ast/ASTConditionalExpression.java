/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a conditional expression, aka ternary expression. This expression
 * obeys the following precedence rules (which are overridden by parentheses):
 * <ul>
 * <li>The condition and the first branch ("then") must be expressions
 * of strictly lower precedence as a conditional expression.
 * <li>The second branch ("else") must be an expression of lower or equal
 * precedence as a conditional expression.
 * </ul>
 *
 * <pre class="grammar">
 *
 * ConditionalExpression ::= {@linkplain ASTExpression Expression} "?"  {@linkplain ASTExpression Expression} ":" {@linkplain ASTExpression Expression}
 *
 * </pre>
 */
public final class ASTConditionalExpression extends AbstractJavaExpr implements ASTExpression {


    ASTConditionalExpression(int id) {
        super(id);
    }

    ASTConditionalExpression(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * Returns the node that represents the guard of this conditional.
     * That is the expression before the '?'.
     */
    public ASTExpression getCondition() {
        return (ASTExpression) jjtGetChild(0);
    }


    /**
     * Returns the node that represents the expression that will be evaluated
     * if the guard evaluates to true.
     */
    public ASTExpression getThenBranch() {
        return (ASTExpression) jjtGetChild(1);
    }


    /**
     * Returns the node that represents the expression that will be evaluated
     * if the guard evaluates to false.
     */
    public ASTExpression getElseBranch() {
        return (ASTExpression) jjtGetChild(2);
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
