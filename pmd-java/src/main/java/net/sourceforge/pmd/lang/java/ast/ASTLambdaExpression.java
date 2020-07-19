/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;


/**
 * A lambda expression.
 *
 *
 * <pre class="grammar">
 *
 * LambdaExpression ::= {@link ASTLambdaParameterList LambdaParameterList} "->" ( {@link ASTExpression Expression} | {@link ASTBlock Block} )
 *
 * </pre>
 */
public final class ASTLambdaExpression extends AbstractJavaExpr {


    ASTLambdaExpression(int id) {
        super(id);
    }

    public ASTLambdaParameterList getParameters() {
        return (ASTLambdaParameterList) getChild(0);
    }


    /** Returns true if this lambda has a block for body. */
    public boolean isBlockBody() {
        return getChild(1) instanceof ASTBlock;
    }

    /** Returns true if this lambda has an expression for body. */
    public boolean isExpressionBody() {
        return !isBlockBody();
    }

    /** Returns the body of this expression, if it is a block. */
    @Nullable
    public ASTBlock getBlock() {
        return AstImplUtil.getChildAs(this, 1, ASTBlock.class);
    }

    /** Returns the body of this expression, if it is an expression. */
    @Nullable
    public ASTExpression getExpression() {
        return AstImplUtil.getChildAs(this, 1, ASTExpression.class);
    }


    /**
     * Returns the body of this lambda if it is a block.
     */
    @Nullable
    public ASTBlock getBlockBody() {
        return NodeStream.of(getLastChild()).filterIs(ASTBlock.class).first();
    }

    /**
     * Returns the body of this lambda if it is an expression.
     */
    @Nullable
    public ASTExpression getExpressionBody() {
        return NodeStream.of(getLastChild()).filterIs(ASTExpression.class).first();
    }

    @Override
    public boolean isFindBoundary() {
        return true;
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
