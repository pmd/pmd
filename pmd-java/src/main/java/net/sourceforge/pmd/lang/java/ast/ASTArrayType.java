/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

/**
 * Represents an array type.
 *
 * <pre class="grammar">
 *
 * ArrayType ::= {@link ASTPrimitiveType PrimitiveType} {@link ASTArrayDimensions ArrayDimensions}
 *             | {@link ASTClassOrInterfaceType ClassOrInterfaceType} {@link ASTArrayDimensions ArrayDimensions}
 *
 * </pre>
 */
public final class ASTArrayType extends AbstractJavaTypeNode implements ASTReferenceType, LeftRecursiveNode {
    ASTArrayType(int id) {
        super(id);
    }


    ASTArrayType(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public List<ASTAnnotation> getDeclaredAnnotations() {
        // an array type's annotations are on its dimensions
        // any annotations found before the element type apply to the
        // element type
        return ((ASTArrayTypeDim) getDimensions().getLastChild()).getDeclaredAnnotations();
    }

    public ASTArrayDimensions getDimensions() {
        return (ASTArrayDimensions) jjtGetChild(1);
    }


    public ASTType getElementType() {
        return (ASTType) jjtGetChild(0);
    }

    @Override
    public String getTypeImage() {
        return getElementType().getTypeImage();
    }

    @Override
    public int getArrayDepth() {
        return getDimensions().getSize();
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


}
