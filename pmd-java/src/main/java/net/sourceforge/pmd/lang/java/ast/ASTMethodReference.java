/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Method or constructor reference expression.
 *
 * <pre class="grammar">
 *
 * MethodReference ::= {@link ASTPrimaryExpression PrimaryExpression} "::" {@link ASTTypeArguments TypeArguments}? &lt;IDENTIFIER&gt;
 *                   | {@link ASTTypeExpression TypeExpression} "::" {@link ASTTypeArguments TypeArguments}? "new"
 *
 * </pre>
 */
public final class ASTMethodReference extends AbstractJavaExpr implements ASTPrimaryExpression, ASTQualifiableExpression, LeftRecursiveNode {

    ASTMethodReference(int id) {
        super(id);
    }

    ASTMethodReference(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public void jjtClose() {
        super.jjtClose();
        JavaNode lhs = jjtGetChild(0);
        // if constructor ref, then the LHS is unambiguously a type.
        if (lhs instanceof ASTAmbiguousName) {
            if (isConstructorReference()) {
                replaceChildAt(0, new ASTTypeExpression(((ASTAmbiguousName) lhs).forceTypeContext()));
            }
        } else if (lhs instanceof ASTType) {
            replaceChildAt(0, new ASTTypeExpression((ASTType) lhs));
        }
    }

    /**
     * Returns true if this is a constructor reference,
     * e.g. {@code ArrayList::new}.
     */
    public boolean isConstructorReference() {
        return "new".equals(getImage());
    }

    /**
     * Returns the node to the left of the "::". This may be a
     * {@link ASTTypeExpression type expression}, or an
     * {@link ASTAmbiguousName ambiguous name}.
     *
     * <p>Note that if this is a {@linkplain #isConstructorReference() constructor reference},
     * then this can only return a {@linkplain ASTTypeExpression type expression}.
     */
    @NonNull
    @Override
    public ASTPrimaryExpression getLhs() {
        return (ASTPrimaryExpression) jjtGetChild(0);
    }


    /**
     * Returns the explicit type arguments mentioned after the "::" if they exist.
     * Type arguments mentioned before the "::", if any, are contained within
     * the {@linkplain #getLhs() lhs type}.
     */
    @Nullable
    public ASTTypeArguments getTypeArguments() {
        return getFirstChildOfType(ASTTypeArguments.class);
    }


    /**
     * Returns the method name, or an empty optional if this is a
     * {@linkplain #isConstructorReference() constructor reference}.
     */
    @Nullable
    public String getMethodName() {
        return getImage().equals("new") ? null : getImage();
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }
}
