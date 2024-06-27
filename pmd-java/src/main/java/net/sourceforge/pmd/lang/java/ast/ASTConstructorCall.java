/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
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
 *      "new" {@link ASTTypeArguments TypeArguments}? {@link ASTClassType ClassType} {@link ASTArgumentList ArgumentList} {@link ASTAnonymousClassDeclaration AnonymousClassDeclaration}?
 *
 * </pre>
 */
public final class ASTConstructorCall extends AbstractInvocationExpr implements QualifiableExpression, LeftRecursiveNode {

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
        return getChild(0) instanceof ASTExpression;
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

    @Override
    public @Nullable ASTTypeArguments getExplicitTypeArguments() {
        return firstChild(ASTTypeArguments.class);
    }


    @Override
    public @NonNull ASTArgumentList getArguments() {
        JavaNode child = getLastChild();
        if (child instanceof ASTAnonymousClassDeclaration) {
            return (ASTArgumentList) getChild(getNumChildren() - 2);
        }
        return (ASTArgumentList) child;
    }

    /** Returns true if type arguments to the constructed instance's type are inferred. */
    public boolean usesDiamondTypeArgs() {
        ASTTypeArguments targs = getTypeNode().getTypeArguments();
        return targs != null && targs.isDiamond();
    }


    /**
     * Returns the type node.
     */
    public ASTClassType getTypeNode() {
        return firstChild(ASTClassType.class);
    }


    /**
     * Returns true if this expression defines a body,
     * which is compiled to an anonymous class. Otherwise, this
     * method returns false.
     */
    public boolean isAnonymousClass() {
        return getLastChild() instanceof ASTAnonymousClassDeclaration;
    }


    @Nullable
    public ASTAnonymousClassDeclaration getAnonymousClassDeclaration() {
        return isAnonymousClass()
               ? (ASTAnonymousClassDeclaration) getLastChild()
               : null;
    }
}
