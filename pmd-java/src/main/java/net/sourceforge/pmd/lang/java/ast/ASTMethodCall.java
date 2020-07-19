/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.InternalInterfaces.QualifierOwner;

/**
 * A method invocation expression. This node represents both qualified (with a left-hand side)
 * and unqualified invocation expressions.
 *
 * <pre class="grammar">
 *
 *
 * MethodCall ::=  &lt;IDENTIFIER&gt; {@link ASTArgumentList ArgumentList}
 *              |  {@link ASTExpression Expression} "." {@link ASTTypeArguments TypeArguments}? &lt;IDENTIFIER&gt; {@link ASTArgumentList ArgumentList}
 *
 * </pre>
 */
public final class ASTMethodCall extends AbstractJavaExpr implements ASTPrimaryExpression, QualifierOwner {

    ASTMethodCall(int id) {
        super(id);
    }


    @Override
    public void jjtClose() {
        super.jjtClose();

        // we need to set the name.

        if (getImage() != null || getChild(0) instanceof ASTSuperExpression) {
            return;
        }

        // Otherwise, the method call was parsed as an ambiguous name followed by arguments
        // The LHS stays ambiguous

        // the cast serves as an assert
        ASTAmbiguousName fstChild = (ASTAmbiguousName) getChild(0);

        fstChild.shrinkOrDeleteInParentSetImage();

        assert getImage() != null;

    }

    /**
     * Returns the name of the called method.
     */
    public String getMethodName() {
        return getImage();
    }

    public ASTArgumentList getArguments() {
        return (ASTArgumentList) getChild(getNumChildren() - 1);
    }


    @Nullable
    public ASTTypeArguments getExplicitTypeArguments() {
        return getFirstChildOfType(ASTTypeArguments.class);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
