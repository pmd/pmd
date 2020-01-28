/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents an array dimension initialized with an expression in an
 * {@linkplain ASTArrayAllocation array allocation expression}. This
 * is always a child of {@link ASTArrayDimensions ArrayDimensions}.
 *
 * TODO not sure we need a separate node type here?
 *
 * <pre class="grammar">
 *
 * ArrayDimExpr ::= {@link ASTAnnotation TypeAnnotation}* "[" {@link ASTExpression Expression} "]"
 *
 * </pre>
 */
public final class ASTArrayDimExpr extends ASTArrayTypeDim implements Annotatable {

    ASTArrayDimExpr(int id) {
        super(id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    public ASTExpression getLengthExpression() {
        return (ASTExpression) getChild(getNumChildren() - 1);
    }

}
