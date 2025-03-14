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
 */
public class ASTArrayTypeDim extends AbstractJavaNode implements Annotatable {

    private boolean isVarargs;

    ASTArrayTypeDim(int id) {
        super(id);
    }

    /**
     * Returns true if this is a varargs dimension. Varargs parameters
     * are represented as an array type whose last dimension has this
     * attribute set to true. Querying {@link ASTFormalParameter#isVarargs()}
     * is more convenient.
     */
    public boolean isVarargs() {
        return isVarargs;
    }

    void setVarargs() {
        isVarargs = true;
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
