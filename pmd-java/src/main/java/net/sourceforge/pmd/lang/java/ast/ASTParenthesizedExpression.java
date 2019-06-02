/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * An expression enclosed in parentheses.
 *
 * <pre class="grammar">
 *
 * ParenthesizedExpression ::= "(" {@link ASTExpression Expression} ")"
 *
 * </pre>
 */
public final class ASTParenthesizedExpression extends AbstractJavaTypeNode implements ASTPrimaryExpression {
    ASTParenthesizedExpression(int id) {
        super(id);
    }


    ASTParenthesizedExpression(JavaParser p, int id) {
        super(p, id);
    }


    public ASTExpression getWrappedExpression() {
        return (ASTExpression) jjtGetChild(0);
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
