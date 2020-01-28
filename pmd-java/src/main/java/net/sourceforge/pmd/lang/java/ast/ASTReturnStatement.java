/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A return statement in a method or constructor body.
 *
 *
 * <pre class="grammar">
 *
 * ReturnStatement ::= "return" {@link ASTExpression Expression}? ";"
 *
 * </pre>
 */
public final class ASTReturnStatement extends AbstractStatement {

    ASTReturnStatement(int id) {
        super(id);
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
     * Returns the returned expression, or null if this is a simple return.
     */
    @Nullable
    public ASTExpression getExpr() {
        return AstImplUtil.getChildAs(this, 0, ASTExpression.class);
    }
}
