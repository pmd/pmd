/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;


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

    private JMethodSig functionalMethod;

    ASTLambdaExpression(int id) {
        super(id);
    }

    /**
     * Returns the type of the functional interface.
     * E.g. in {@code stringStream.map(s -> s.isEmpty())}, this is
     * {@code java.util.function.Function<java.lang.String, java.lang.Boolean>}.
     *
     * @see #getFunctionalMethod()
     */
    @Override
    public @NonNull JTypeMirror getTypeMirror() {
        return super.getTypeMirror();
    }

    /**
     * Returns the method that is overridden in the functional interface.
     * E.g. in {@code stringStream.map(s -> s.isEmpty())}, this is
     * {@code java.util.function.Function#apply(java.lang.String) ->
     * java.lang.Boolean}
     *
     * @see #getTypeMirror()
     */
    public JMethodSig getFunctionalMethod() {
        // force evaluation
        getTypeMirror();
        return functionalMethod;
    }

    void setFunctionalMethod(JMethodSig functionalMethod) {
        this.functionalMethod = functionalMethod;
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
