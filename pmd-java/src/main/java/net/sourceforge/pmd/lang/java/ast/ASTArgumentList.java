/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * The argument list of a {@linkplain ASTMethodCall method}, {@linkplain ASTConstructorCall constructor call},
 * or {@linkplain ASTExplicitConstructorInvocation explicit constructor invocation}.
 *
 * <pre class="grammar">
 *
 * ArgumentList ::= "(" ( {@link ASTExpression Expression} ( "," {@link ASTExpression Expression})* )? ")"
 *
 * </pre>
 */
public final class ASTArgumentList extends ASTList<ASTExpression> {

    ASTArgumentList(int id) {
        super(id, ASTExpression.class);
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
