/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;


/**
 * Formal parameter of a lambda expression. Child of {@link ASTLambdaParameterList}.
 *
 * <pre class="grammar">
 *
 * LambdaParameter ::= ( "final" | {@link ASTAnnotation Annotation} )* ( "var" | {@link ASTType Type} ) {@link ASTVariableDeclaratorId VariableDeclaratorId}
 *                   | {@link ASTVariableDeclaratorId VariableDeclaratorId}
 *
 * </pre>
 */
public final class ASTLambdaParameter extends AbstractJavaTypeNode
    implements InternalInterfaces.VariableIdOwner {

    private boolean isFinal;

    ASTLambdaParameter(int id) {
        super(id);
    }

    ASTLambdaParameter(JavaParser p, int id) {
        super(p, id);
    }


    void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }


    /**
     * If true, this formal parameter represents one without explicit types.
     * This can appear as part of a lambda expression with java11 using "var".
     *
     * @see ASTVariableDeclaratorId#isTypeInferred()
     */
    public boolean isTypeInferred() {
        return getTypeNode() == null;
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    /**
     * Returns the declarator ID of this formal parameter.
     */
    @Override
    @NonNull
    public ASTVariableDeclaratorId getVarId() {
        return getFirstChildOfType(ASTVariableDeclaratorId.class);
    }

    /** Returns the type node of this formal parameter. */
    @Nullable
    public ASTType getTypeNode() {
        return getFirstChildOfType(ASTType.class);
    }

    @Override
    public JavaTypeDefinition getTypeDefinition() {
        return getVarId().getTypeDefinition();
    }


}
