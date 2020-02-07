/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.qname.JavaOperationQualifiedName;


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
public final class ASTLambdaExpression extends AbstractJavaExpr implements ASTExpression, MethodLikeNode {

    private JavaOperationQualifiedName qualifiedName;

    ASTLambdaExpression(int id) {
        super(id);
    }


    public ASTLambdaParameterList getParameters() {
        return (ASTLambdaParameterList) getChild(0);
    }


    public boolean isBlockBody() {
        return getChild(1) instanceof ASTBlock;
    }

    public boolean isExpressionBody() {
        return !isBlockBody();
    }


    /**
     * Returns the body of this lambda if it is a block.
     */
    @Nullable
    public ASTBlock getBlockBody() {
        return NodeStream.of(getLastChild()).filterIs(ASTBlock.class).first();
    }

    /**
     * Returns the body of this lambda if it is an expression.
     */
    @Nullable
    public ASTExpression getExpressionBody() {
        return NodeStream.of(getLastChild()).filterIs(ASTExpression.class).first();
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
    public JavaOperationQualifiedName getQualifiedName() {
        return qualifiedName;
    }

    void setQualifiedName(JavaOperationQualifiedName qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    @Override
    public MethodLikeKind getKind() {
        return MethodLikeKind.LAMBDA;
    }
}
