/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * Formal parameter of a lambda expression. Child of {@link ASTLambdaParameterList}.
 *
 * <pre class="grammar">
 *
 * LambdaParameter ::= {@link ASTModifierList LocalVarModifierList} ( "var" | {@link ASTType Type} ) {@link ASTVariableId VariableId}
 *                   | {@link ASTModifierList EmptyModifierList} {@link ASTVariableId VariableId}
 *
 * </pre>
 */
public final class ASTLambdaParameter extends AbstractJavaTypeNode
    implements InternalInterfaces.VariableIdOwner, ModifierOwner {

    ASTLambdaParameter(int id) {
        super(id);
    }

    /**
     * If true, this formal parameter represents one without explicit types.
     * This can appear as part of a lambda expression with java11 using "var".
     *
     * @see ASTVariableId#isTypeInferred()
     */
    public boolean isTypeInferred() {
        return getTypeNode() == null;
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the lambda that owns this parameter.
     */
    public ASTLambdaExpression getOwner() {
        return (ASTLambdaExpression) getParent().getParent();
    }

    /**
     * Returns the declarator ID of this formal parameter.
     */
    @Override
    @NonNull
    public ASTVariableId getVarId() {
        return firstChild(ASTVariableId.class);
    }

    /** Returns the type node of this formal parameter. */
    @Nullable
    public ASTType getTypeNode() {
        return firstChild(ASTType.class);
    }

    public boolean isFinal() {
        return hasModifiers(JModifier.FINAL);
    }
}
