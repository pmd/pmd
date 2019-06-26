/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a pre-decrement expression on a variable.
 * This has the same precedence as {@linkplain ASTUnaryExpression UnaryExpression}
 * and the like.
 *
 * <pre class="grammar">
 *
 * PreDecrementExpression ::= "--" {@linkplain ASTPrimaryExpression PrimaryExpression}
 *
 * </pre>
 *
 * @deprecated Merged into {@link ASTIncrementExpression IncrementExpression}
 */
@Deprecated
public final class ASTPreDecrementExpression extends AbstractJavaExpr implements ASTExpression {

    ASTPreDecrementExpression(int id) {
        super(id);
    }

    ASTPreDecrementExpression(JavaParser p, int id) {
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
}
