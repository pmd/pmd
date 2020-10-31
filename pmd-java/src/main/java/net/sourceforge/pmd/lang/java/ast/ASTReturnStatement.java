/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;

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
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the method, ctor or lambda that this statement terminates.
     */
    public JavaNode getTarget() {
        return ancestors().map(NodeStream.asInstanceOf(ASTMethodOrConstructorDeclaration.class, ASTLambdaExpression.class)).first();
    }

    /**
     * Returns the returned expression, or null if this is a simple return.
     */
    @Nullable
    public ASTExpression getExpr() {
        return AstImplUtil.getChildAs(this, 0, ASTExpression.class);
    }
}
