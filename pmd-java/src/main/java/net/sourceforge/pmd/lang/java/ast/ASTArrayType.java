/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents an array type.
 *
 * <pre>
 *
 * ArrayType ::= {@link ASTPrimitiveType PrimitiveType} {@link ASTArrayTypeDims ArrayTypeDims}
 *             | {@link ASTClassOrInterfaceType ClassOrInterfaceType} {@link ASTArrayTypeDims ArrayTypeDims}
 *
 * </pre>
 */
public final class ASTArrayType extends AbstractJavaTypeNode {
    ASTArrayType(int id) {
        super(id);
    }


    ASTArrayType(JavaParser p, int id) {
        super(p, id);
    }


    public ASTArrayTypeDims getDimensions() {
        return (ASTArrayTypeDims) jjtGetChild(1);
    }


    public int getArrayDepth() {
        return getDimensions().getSize();
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


}
