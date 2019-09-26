/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An array creation expression. The dimensions of the array type may
 * be initialized with a length expression (in which case they're
 * {@link ASTArrayDimExpr ArrayDimExpr} nodes).
 *
 * <pre class="grammar">
 *
 * ArrayCreationExpression ::= "new" {@link ASTArrayType ArrayType} ({@link ASTArrayInitializer ArrayInitializer})
 *
 * </pre>
 */
public final class ASTArrayAllocation extends AbstractJavaExpr implements ASTPrimaryExpression {


    ASTArrayAllocation(int id) {
        super(id);
    }


    ASTArrayAllocation(JavaParser p, int id) {
        super(p, id);
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
     * Returns the node representing the array type being instantiated.
     */
    public ASTArrayType getTypeNode() {
        return getFirstChildOfType(ASTArrayType.class);
    }

    @Nullable
    public ASTArrayInitializer getArrayInitializer() {
        return AstImplUtil.getChildAs(this, jjtGetNumChildren() - 1, ASTArrayInitializer.class);
    }

    /**
     * Returns the number of dimensions of the created array.
     */
    public int getArrayDepth() {
        return getTypeNode().getArrayDepth();
    }

}
