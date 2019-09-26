/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents an array dimension in an {@linkplain ASTArrayType array type},
 * or in an {@linkplain ASTArrayAllocation array allocation expression}.
 *
 * <p>{@linkplain ASTArrayDimExpr ArrayDimExpr} represents array dimensions
 * that are initialized with a length, in array allocation expressions.
 *
 * <pre class="grammar">
 *
 * ArrayTypeDim ::= {@link ASTAnnotation TypeAnnotation}* "[" "]"
 *
 * </pre>
 *
 */
public class ASTArrayTypeDim extends AbstractJavaNode implements Annotatable {

    ASTArrayTypeDim(int id) {
        super(id);
    }

    ASTArrayTypeDim(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
