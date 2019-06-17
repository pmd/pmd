/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An array creation expression.
 *
 * <pre class="grammar">
 *
 * ArrayCreationExpression ::= "new" {@link ASTAnnotation TypeAnnotation}*
 *                             ({@link ASTPrimitiveType PrimitiveType} | {@link ASTClassOrInterfaceType ClassOrInterfaceType})
 *                             {@link ASTArrayAllocationDims ArrayAllocationDims}
 *                             ({@link ASTArrayInitializer ArrayInitializer})
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
     * Returns the node representing the element type of the array. This
     * is never an {@link ASTArrayType array type}. The dimensions of the
     * instantiated array are carried by {@link #getArrayDims() another node}.
     */
    public ASTType getElementTypeNode() {
        return getFirstChildOfType(ASTType.class);
    }


    /**
     * Returns the dimensions of the array.
     */
    public ASTArrayAllocationDims getArrayDims() {
        return getFirstChildOfType(ASTArrayAllocationDims.class);
    }

    @Nullable
    public ASTArrayInitializer getArrayInitializer() {
        return AstImplUtil.getChildAs(this, jjtGetNumChildren() - 1, ASTArrayInitializer.class);
    }

    /**
     * Returns the number of dimensions of the created array.
     */
    public int getArrayDepth() {
        return getArrayDims().getArrayDepth();
    }

}
