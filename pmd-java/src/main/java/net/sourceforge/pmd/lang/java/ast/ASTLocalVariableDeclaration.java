/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Represents a local variable declaration. This is a {@linkplain ASTBlockStatement block statement},
 * but the node is also used in {@linkplain ASTForInit for-loop initialisers} and
 * {@linkplain ASTForStatement foreach statements}.
 *
 * <p>This statement may define several variables, possibly of different types (see {@link
 * ASTVariableDeclaratorId#getType()}).
 * The nodes corresponding to the declared variables are accessible through {@link #iterator()}.
 *
 * <pre>
 *
 * LocalVariableDeclaration ::= ( "final" | {@linkplain ASTAnnotation Annotation} )* {@linkplain ASTType Type} {@linkplain ASTVariableDeclarator VariableDeclarator} ( "," {@linkplain ASTVariableDeclarator VariableDeclarator} )*
 *
 * </pre>
 */
public class ASTLocalVariableDeclaration extends AbstractJavaAccessNode implements Dimensionable, CanSuppressWarnings, Iterable<ASTVariableDeclaratorId> {

    @InternalApi
    @Deprecated
    public ASTLocalVariableDeclaration(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTLocalVariableDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public boolean hasSuppressWarningsAnnotationFor(Rule rule) {
        for (int i = 0; i < getNumChildren(); i++) {
            if (getChild(i) instanceof ASTAnnotation) {
                ASTAnnotation a = (ASTAnnotation) getChild(i);
                if (a.suppresses(rule)) {
                    return true;
                }
            }
        }
        return false;
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
        return (ASTVariableDeclaratorId) getChild(getNumChildren() - 1).getChild(0);
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
