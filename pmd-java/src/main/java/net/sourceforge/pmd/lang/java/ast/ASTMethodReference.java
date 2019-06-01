/**
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
 *
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


    /**
     * Returns true if this is a constructor reference,
     * e.g. {@code ArrayList::new}.
     */
    public boolean isConstructorReference() {
        return getImage().equals("new");
    }


    /**
     * Returns the type node to the left of the "::" if it exists.
     * Otherwise, this method returns null and {@link #getLhsExpression()}
     * returns non-null.
     */
    @Nullable
    public ASTReferenceType getLhsType() {
        return AstImplUtil.getChildAs(this, 0, ASTReferenceType.class);
    }


    /**
     * Returns the expression node to the left of the "::" if it exists.
     * Otherwise, this method returns null and {@link #getLhsType()}
     * returns non-null.
     */
    @Override
    @Nullable
    public ASTPrimaryExpression getLhsExpression() {
        return ASTQualifiableExpression.super.getLhsExpression();
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
