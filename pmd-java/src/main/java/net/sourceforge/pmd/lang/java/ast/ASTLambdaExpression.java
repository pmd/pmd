/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;


/**
 * A lambda expression.
 *
 *
 * <pre class="grammar">
 *
 * LambdaExpression ::= {@link ASTLambdaParameterList LambdaParameterList} "->" ( {@link ASTExpression Expression} | {@link ASTBlock Block} )
 *
 * </pre>
 */
public final class ASTLambdaExpression extends AbstractMethodLikeNode implements ASTExpression {

    private JavaTypeDefinition typeDefinition;


    ASTLambdaExpression(int id) {
        super(id);
    }


    ASTLambdaExpression(JavaParser p, int id) {
        super(p, id);
    }


    public ASTLambdaParameterList getParameters() {
        return (ASTLambdaParameterList) jjtGetChild(0);
    }


    public boolean isBlockBody() {
        return jjtGetChild(1) instanceof ASTBlock;
    }

    public boolean isExpressionBody() {
        return !isBlockBody();
    }

    // TODO these are copied from AbstractJavaTypeNode because it's easier to extend abstractMethodLikeNode


    @Override
    @Nullable
    public Class<?> getType() {
        return typeDefinition == null ? null : typeDefinition.getType();
    }


    @Override
    @Nullable
    public JavaTypeDefinition getTypeDefinition() {
        return typeDefinition;
    }


    void setTypeDefinition(JavaTypeDefinition typeDefinition) {
        this.typeDefinition = typeDefinition;
    }


    @Override
    public boolean isFindBoundary() {
        return true;
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    @Override
    public MethodLikeKind getKind() {
        return MethodLikeKind.LAMBDA;
    }
}
