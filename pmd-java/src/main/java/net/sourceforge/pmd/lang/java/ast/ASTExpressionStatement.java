/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A statement that contains an expression. Note that this is not an
 * expression itself.
 *
 * <pre class="grammar">
 *
 * ExpressionStatement ::= {@link ASTExpression StatementExpression} ";"
 *
 * </pre>
 */
public final class ASTExpressionStatement extends AbstractStatement {

    ASTExpressionStatement(int id) {
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


    /** Returns the contained expression. */
    @NonNull
    public ASTExpression getExpr() {
        return (ASTExpression) getChild(0);
    }
}
