/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Method or constructor reference expression.
 *
 * <pre class="grammar">
 *
 * MethodReference ::= {@link ASTPrimaryExpression PrimaryExpression} "::" {@link ASTTypeArguments TypeArguments}? &lt;IDENTIFIER&gt;
 *                   | {@link ASTReferenceType ReferenceType} "::" {@link ASTTypeArguments TypeArguments}? &lt;IDENTIFIER&gt;
 *                   | {@link ASTClassOrInterfaceType ClassType} "::" {@link ASTTypeArguments TypeArguments}? "new"
 *                   | {@link ASTArrayType ArrayType} "::" "new"
 *                   | ({@link ASTClassOrInterfaceType TypeName} ".")? {@link ASTSuperExpression "super"} :: {@link ASTTypeArguments TypeArguments}? &lt;IDENTIFIER&gt;
 * </pre>
 */
public final class ASTMethodReference extends AbstractJavaTypeNode implements ASTPrimaryExpression, ASTQualifiableExpression, LeftRecursiveNode {

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
        ASTAmbiguousName lhs = getAmbiguousLhs();
        // if constructor ref, then the LHS is unambiguously a type.
        if (isConstructorReference() && lhs != null) {
            replaceChildAt(0, lhs.forceTypeContext());
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
     * Returns the ambiguous name to the left of the "::" if it exists.
     * If the LHS has been disambiguated, then this returns null and either
     * {@link #getLhsExpression()} or {@link #getLhsType()} will return a value.
     *
     * <p>Note that if this is a {@linkplain #isConstructorReference() constructor reference},
     * then this can only return non-null.
     */
    @Nullable
    public ASTAmbiguousName getAmbiguousLhs() {
        return AstImplUtil.getChildAs(this, 0, ASTAmbiguousName.class);
    }

    /**
     * Returns the type node to the left of the "::" if it exists and is
     * unambiguous. If ambiguous, then this method returns {@code null}
     * and {@link #getAmbiguousLhs()} will return the LHS. If the LHS
     * is an unambiguous expression, then {@link #getLhsExpression()}
     * will return it.
     *
     * <p>Note that if this is a {@linkplain #isConstructorReference() constructor reference},
     * then this can only return non-null.
     */
    @Nullable
    public ASTReferenceType getLhsType() {
        ASTReferenceType lhs = AstImplUtil.getChildAs(this, 0, ASTReferenceType.class);
        return lhs instanceof ASTAmbiguousName ? null : lhs;
    }


    /**
     * Returns the expression to the left of the "::" if it exists and is
     * unambiguous. If ambiguous, then this method returns {@code null}
     * and {@link #getAmbiguousLhs()} will return the LHS. If the LHS
     * is an unambiguous type, then {@link #getLhsType()} will return it.
     *
     * <p>Note that if this is a {@linkplain #isConstructorReference() constructor reference},
     * then this can only return null.
     */
    @Override
    @Nullable
    public ASTPrimaryExpression getLhsExpression() {
        ASTPrimaryExpression lhs = ASTQualifiableExpression.super.getLhsExpression();
        return lhs instanceof ASTAmbiguousName ? null : lhs;
    }


    /**
     * Returns the explicit type arguments mentioned after the "::" if they exist.
     * Type arguments mentioned before the "::", if any, are contained within
     * the {@linkplain #getLhsType() lhs type}.
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
