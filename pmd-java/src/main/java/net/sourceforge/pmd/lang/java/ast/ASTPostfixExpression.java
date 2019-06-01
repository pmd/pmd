/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a unary postfix operation on a value.
 * This is one of the {@linkplain ASTUnaryExpression PrefixExpression}
 * and has the same precedence.
 *
 * <pre class="grammar">
 *
 * PostfixExpression ::= {@linkplain ASTPrimaryExpression PrimaryExpression} ( "++" | "--" )
 *
 * </pre>
 */
public final class ASTPostfixExpression extends AbstractJavaTypeNode implements ASTExpression {

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


    /**
     * Returns the image of this unary operator, i.e. "++" or "--".
     */
    public String getOperator() {
        return getImage();
    }

    /**
     * Returns the operator of this postfix expression.
     */
    public UnaryOp getOp() {
        return UnaryOp.fromImage(getImage());
    }

    public ASTPrimaryExpression getBaseExpression() {
        return (ASTPrimaryExpression) jjtGetChild(0);
    }

}
