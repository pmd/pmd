/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a conditional expression, aka ternary expression.
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
        return (ASTExpression) getChild(1);
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
