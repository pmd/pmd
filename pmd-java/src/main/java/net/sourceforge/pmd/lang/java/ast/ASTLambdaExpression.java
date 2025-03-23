/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;


/**
 * A lambda expression.
 *
 *
 * <pre class="grammar">
 *
 * LambdaExpression ::= {@link ASTLambdaParameterList LambdaParameterList} {@code  "->"} ( {@link ASTExpression Expression} | {@link ASTBlock Block} )
 *
 * </pre>
 */
public final class ASTLambdaExpression extends AbstractJavaExpr implements FunctionalExpression {

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
    @Override
    public JMethodSig getFunctionalMethod() {
        forceTypeResolution();
        return assertNonNullAfterTypeRes(functionalMethod);
    }

    void setFunctionalMethod(@Nullable JMethodSig functionalMethod) {
        this.functionalMethod = functionalMethod;
    }

    public ASTLambdaParameterList getParameters() {
        return (ASTLambdaParameterList) getChild(0);
    }


    /**
     * Return true if this lambda is explicitly typed, meaning
     * all parameters have an explicit type. Note that lambdas
     * with zero parameters are explicitly typed.
     */
    public boolean isExplicitlyTyped() {
        return getParameters().toStream().none(ASTLambdaParameter::isTypeInferred);
    }

    /** Returns true if this lambda has a block for body. */
    public boolean isBlockBody() {
        return getChild(1) instanceof ASTBlock;
    }

    /** Returns true if this lambda has an expression for body. */
    public boolean isExpressionBody() {
        return !isBlockBody();
    }

    /**
     * Returns the body of this expression, if it is a block.
     *
     * @deprecated Use {@link #getBlockBody()}
     */
    @Deprecated
    public @Nullable ASTBlock getBlock() {
        return getBlockBody();
    }

    /**
     * Returns the body of this expression, if it is an expression.
     *
     * @deprecated Use {@link #getExpressionBody()}
     */
    @Deprecated
    public @Nullable ASTExpression getExpression() {
        return getExpressionBody();
    }


    /**
     * Returns the body of this lambda if it is a block.
     */
    public @Nullable ASTBlock getBlockBody() {
        return AstImplUtil.getChildAs(this, 1, ASTBlock.class);
    }

    /**
     * Returns the body of this lambda if it is an expression.
     */
    public @Nullable ASTExpression getExpressionBody() {
        return AstImplUtil.getChildAs(this, 1, ASTExpression.class);
    }

    @Override
    public boolean isFindBoundary() {
        return true;
    }


    /**
     * Returns the number of formal parameters of this lambda.
     */
    public int getArity() {
        return getParameters().size();
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
