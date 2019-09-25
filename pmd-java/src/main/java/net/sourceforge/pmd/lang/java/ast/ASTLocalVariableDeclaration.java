/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;


/**
 * Represents a local variable declaration. This is a {@linkplain ASTBlockStatement block statement},
 * but the node is also used in {@linkplain ASTForInit for-loop initialisers} and
 * {@linkplain ASTForStatement foreach statements}.
 *
 * <p>This statement may define several variables, possibly of different types (see {@link
 * ASTVariableDeclaratorId#getType()}).
 * The nodes corresponding to the declared variables are accessible through {@link #iterator()}.
 *
 * <pre class="grammar">
 *
 * LocalVariableDeclaration ::= ( "final" | {@linkplain ASTAnnotation Annotation} )* {@linkplain ASTType Type} {@linkplain ASTVariableDeclarator VariableDeclarator} ( "," {@linkplain ASTVariableDeclarator VariableDeclarator} )*
 *
 * </pre>
 */
public final class ASTLocalVariableDeclaration extends AbstractJavaAccessNode implements Dimensionable, Iterable<ASTVariableDeclaratorId> {

    ASTLocalVariableDeclaration(int id) {
        super(id);
    }

    ASTLocalVariableDeclaration(JavaParser p, int id) {
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
     * If true, this local variable declaration represents a declaration,
     * which makes use of local variable type inference, e.g. java10 "var".
     * You can receive the inferred type via {@link ASTVariableDeclarator#getType()}.
     *
     * @see ASTVariableDeclaratorId#isTypeInferred()
     */
    public boolean isTypeInferred() {
        return getTypeNode() == null;
    }

    @Override
    @Deprecated
    public boolean isArray() {
        return getArrayDepth() > 0;
    }

    @Override
    @Deprecated
    public int getArrayDepth() {
        return getArrayDimensionOnType() + getArrayDimensionOnDeclaratorId();
    }

    /**
     * Gets the type node for this variable declaration statement.
     * With Java10 and local variable type inference, there might be
     * no type node at all.
     *
     * @return The type node or <code>null</code>
     *
     * @see #isTypeInferred()
     */
    public ASTType getTypeNode() {
        return getFirstChildOfType(ASTType.class);
    }

    private int getArrayDimensionOnType() {
        ASTType typeNode = getTypeNode();
        if (typeNode != null) {
            return typeNode.getArrayDepth();
        }
        return 0;
    }

    private ASTVariableDeclaratorId getDecl() {
        return (ASTVariableDeclaratorId) jjtGetChild(jjtGetNumChildren() - 1).jjtGetChild(0);
    }

    private int getArrayDimensionOnDeclaratorId() {
        return getDecl().getArrayDepth();
    }

    /**
     * Gets the variable name of this declaration. This method searches the first
     * VariableDeclartorId node and returns it's image or <code>null</code> if
     * the child node is not found.
     *
     * @return a String representing the name of the variable
     *
     * @deprecated LocalVariableDeclaration may declare several variables, so this is not exhaustive
     *     Iterate on the {@linkplain ASTVariableDeclaratorId VariableDeclaratorIds} instead
     */
    // It would be nice to have a way to inform XPath users of the intended replacement
    // for a deprecated attribute. We may use another annotation for that.
    @Deprecated
    public String getVariableName() {
        ASTVariableDeclaratorId decl = getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        if (decl != null) {
            return decl.getImage();
        }
        return null;
    }


    /**
     * Returns an iterator over the ids of the variables
     * declared in this statement.
     */
    @Override
    public Iterator<ASTVariableDeclaratorId> iterator() {
        return ASTVariableDeclarator.iterateIds(this);
    }
}
