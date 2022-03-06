/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.types.JArrayType;

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
public final class ASTArrayType extends AbstractJavaTypeNode implements ASTReferenceType {
    ASTArrayType(int id) {
        super(id);
    }


    @Override
    public NodeStream<ASTAnnotation> getDeclaredAnnotations() {
        return getDimensions().getLastChild().getDeclaredAnnotations();
    }

    /**
     * Returns the list of dimensions of the array type.
     */
    public @NonNull ASTArrayDimensions getDimensions() {
        return (ASTArrayDimensions) getChild(1);
    }


    /**
     * Returns the element type of the array (see {@link JArrayType#getElementType()}).
     */
    public @NonNull ASTType getElementType() {
        return (ASTType) getChild(0);
    }

    /**
     * Returns the number of dimensions declared by this type.
     */
    public int getArrayDepth() {
        return getDimensions().size();
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
