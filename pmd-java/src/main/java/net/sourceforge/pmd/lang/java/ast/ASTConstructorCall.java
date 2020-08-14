/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A class instance creation expression. Represents both {@linkplain #isQualifiedInstanceCreation() qualified}
 * and unqualified instance creation. May declare an anonymous class body.
 *
 *
 * <pre class="grammar">
 *
 * ConstructorCall   ::= UnqualifiedAlloc
 *                     | {@link ASTExpression Expression} "." UnqualifiedAlloc
 *
 * UnqualifiedAlloc                  ::=
 *      "new" {@link ASTTypeArguments TypeArguments}? {@link ASTClassOrInterfaceType ClassOrInterfaceType} {@link ASTArgumentList ArgumentList} {@link ASTAnonymousClassDeclaration AnonymousClassDeclaration}?
 *
 * </pre>
 */
public final class ASTConstructorCall extends AbstractJavaExpr implements ASTPrimaryExpression, QualifiableExpression, LeftRecursiveNode {

    ASTConstructorCall(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns true if this expression begins with a primary expression.
     * Such an expression creates an instance of inner member classes and
     * their anonymous subclasses. For example, {@code new Outer().new Inner()}
     * evaluates to an instance of the Inner class, which is nested inside
     * the new instance of Outer.
     */
    public boolean isQualifiedInstanceCreation() {
        return getChild(0) instanceof ASTPrimaryExpression;
    }

    /**
     * Returns the outer instance expression, if this is a {@linkplain #isQualifiedInstanceCreation() qualified}
     * constructor call. Otherwise returns null. This can never be a
     * {@linkplain ASTTypeExpression type expression}, and is never
     * {@linkplain ASTAmbiguousName ambiguous}.
     */
    @Override
    public @Nullable ASTExpression getQualifier() {
        return QualifiableExpression.super.getQualifier();
    }

    /**
     * Returns true if this constructor call uses the diamond operator,
     * eg {@code new ArrayList<>()}.
     */
    public boolean isDiamond() {
        ASTTypeArguments targs = getTypeNode().getTypeArguments();
        return targs != null && targs.isDiamond();
    }

    @Nullable
    public ASTTypeArguments getExplicitTypeArguments() {
        return getFirstChildOfType(ASTTypeArguments.class);
    }


    public ASTArgumentList getArguments() {
        int idx = getNumChildren() - (isAnonymousClass() ? 2 : 1);
        return (ASTArgumentList) getChild(idx);
    }

    /** Returns true if type arguments to the constructed instance's type are inferred. */
    public boolean usesDiamondTypeArgs() {
        ASTTypeArguments targs = getTypeNode().getTypeArguments();
        return targs != null && targs.isDiamond();
    }


    /**
     * Returns the type node.
     */
    public ASTClassOrInterfaceType getTypeNode() {
        return getFirstChildOfType(ASTClassOrInterfaceType.class);
    }


    /**
     * Returns true if this expression defines a body,
     * which is compiled to an anonymous class. If this
     * method returns false.
     */
    public boolean isAnonymousClass() {
        return getChild(getNumChildren() - 1) instanceof ASTAnonymousClassDeclaration;
    }


    @Nullable
    public ASTAnonymousClassDeclaration getAnonymousClassDeclaration() {
        return isAnonymousClass()
               ? (ASTAnonymousClassDeclaration) getChild(getNumChildren() - 1)
               : null;
    }
}
