/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 * Method or constructor reference expression.
 *
 * <pre class="grammar">
 *
 * MethodReference ::= {@link ASTExpression Expression} "::" {@link ASTTypeArguments TypeArguments}? &lt;IDENTIFIER&gt;
 *                   | {@link ASTTypeExpression TypeExpression} "::" {@link ASTTypeArguments TypeArguments}? "new"
 *
 * </pre>
 */
public final class ASTMethodReference extends AbstractJavaExpr implements ASTPrimaryExpression, QualifiableExpression, LeftRecursiveNode {

    private JMethodSig functionalMethod;
    private JMethodSig compileTimeDecl;

    ASTMethodReference(int id) {
        super(id);
    }


    @Override
    public void jjtClose() {
        super.jjtClose();
        JavaNode lhs = getChild(0);
        // if constructor ref, then the LHS is unambiguously a type.
        if (lhs instanceof ASTAmbiguousName) {
            if (isConstructorReference()) {
                setChild(new ASTTypeExpression(((ASTAmbiguousName) lhs).forceTypeContext()), 0);
            }
        } else if (lhs instanceof ASTType) {
            setChild(new ASTTypeExpression((ASTType) lhs), 0);
        }
    }

    /**
     * Returns the LHS, whether it is a type or an expression.
     * Returns null if this is an unqualified method call.
     */
    public TypeNode getLhs() {
        return AstImplUtil.getChildAs(this, 0, TypeNode.class);
    }

    /**
     * Returns true if this is a constructor reference,
     * e.g. {@code ArrayList::new}.
     */
    public boolean isConstructorReference() {
        return JavaTokenKinds.NEW == getLastToken().kind;
    }

    /**
     * Returns the node to the left of the "::". This may be a
     * {@link ASTTypeExpression type expression}, or an
     * {@link ASTAmbiguousName ambiguous name}.
     *
     * <p>Note that if this is a {@linkplain #isConstructorReference() constructor reference},
     * then this can only return a {@linkplain ASTTypeExpression type expression}.
     */
    @Override
    public @NonNull ASTExpression getQualifier() {
        return (ASTExpression) getChild(0);
    }


    /**
     * Returns the explicit type arguments mentioned after the "::" if they exist.
     * Type arguments mentioned before the "::", if any, are contained within
     * the {@linkplain #getQualifier() lhs type}.
     */
    public @Nullable ASTTypeArguments getExplicitTypeArguments() {
        return getFirstChildOfType(ASTTypeArguments.class);
    }


    /**
     * Returns the method name, or an {@link JConstructorSymbol#CTOR_NAME}
     * if this is a {@linkplain #isConstructorReference() constructor reference}.
     */
    public @NonNull String getMethodName() {
        return super.getImage();
    }

    @Deprecated
    @Override
    public @Nullable String getImage() {
        return null;
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the type of the functional interface.
     * E.g. in {@code stringStream.map(String::isEmpty)}, this is
     * {@code java.util.function.Function<java.lang.String, java.lang.Boolean>}.
     *
     * @see #getFunctionalMethod()
     * @see #getReferencedMethod()
     */
    @Override
    public @NonNull JTypeMirror getTypeMirror() {
        return super.getTypeMirror();
    }

    /**
     * Returns the method that is overridden in the functional interface.
     * E.g. in {@code stringStream.map(String::isEmpty)}, this is
     * {@code java.util.function.Function#apply(java.lang.String) -> java.lang.Boolean}
     *
     * @see #getReferencedMethod()
     * @see #getTypeMirror()
     */
    public JMethodSig getFunctionalMethod() {
        // force evaluation
        getTypeMirror();
        return functionalMethod;
    }

    /**
     * Returns the method that is referenced.
     * E.g. in {@code stringStream.map(String::isEmpty)}, this is
     * {@code java.lang.String.isEmpty() -> boolean}.
     *
     * <p>This is called the <i>compile-time declaration</i> of the
     * method reference in the JLS.
     *
     * @see #getFunctionalMethod()
     * @see #getTypeMirror()
     */
    public JMethodSig getReferencedMethod() {
        // force evaluation
        getTypeMirror();
        return compileTimeDecl;
    }

    void setFunctionalMethod(JMethodSig methodType) {
        this.functionalMethod = methodType;
    }

    void setCompileTimeDecl(JMethodSig methodType) {
        this.compileTimeDecl = methodType;
    }

}
