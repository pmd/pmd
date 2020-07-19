/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a type cast expression. This is syntactically a unary prefix
 * operation and has the same precedence.
 *
 * <pre class="grammar">
 *
 * CastExpression ::= "(" {@link ASTType Type} ")" {@linkplain ASTExpression Expression}
 *
 * </pre>
 */
public final class ASTCastExpression extends AbstractJavaExpr implements ASTExpression {

    ASTCastExpression(int id) {
        super(id);
    }

    public ASTType getCastType() {
        return getFirstChildOfType(ASTType.class);
    }

    public ASTExpression getOperand() {
        return (ASTExpression) getChild(getNumChildren() - 1);
    }


    @Override
    public <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
