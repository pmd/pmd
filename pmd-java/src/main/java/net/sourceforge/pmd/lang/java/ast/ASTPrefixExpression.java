/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import net.sourceforge.pmd.lang.java.ast.UnaryOp.PrefixOp;

/**
 * Represents a unary prefix operation on a value.
 * This has a precedence greater than {@link ASTInfixExpression}.
 *
 * <p>Prefix expressions have the same precedence as {@linkplain ASTCastExpression CastExpression}.
 *
 * <pre class="grammar">
 *
 * PrefixExpression ::= {@link PrefixOp} UnaryExpression
 *
 * </pre>
 */
public final class ASTPrefixExpression extends AbstractJavaExpr implements ASTUnaryExpression {

    private PrefixOp operator;

    ASTPrefixExpression(int id) {
        super(id);
    }

    ASTPrefixExpression(JavaParser p, int id) {
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
    public PrefixOp getOperator() {
        return operator;
    }

    void setOp(PrefixOp op) {
        this.operator = op;
    }
}
