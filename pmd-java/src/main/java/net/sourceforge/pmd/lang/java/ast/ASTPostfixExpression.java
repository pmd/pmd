/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import net.sourceforge.pmd.lang.java.ast.UnaryOp.PostfixOp;

/**
 * Represents a unary postfix operation on a value.
 * This has a precedence greater than {@link ASTCastExpression CastExpression}.
 *
 * <pre class="grammar">
 *
 * PostfixExpression ::= {@link ASTPrimaryExpression PrimaryExpression} ({@link PostfixOp "++" | "--"})
 *
 * </pre>
 */
public final class ASTPostfixExpression extends AbstractJavaExpr implements ASTUnaryExpression {

    private PostfixOp operator;

    ASTPostfixExpression(int id) {
        super(id);
    }

    ASTPostfixExpression(JavaParser p, int id) {
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

    @Override
    public PostfixOp getOperator() {
        return operator;
    }

    void setOp(PostfixOp op) {
        this.operator = op;
    }
}
