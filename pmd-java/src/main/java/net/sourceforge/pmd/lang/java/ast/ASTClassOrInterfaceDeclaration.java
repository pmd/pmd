/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.Node;


/**
 * Represents class and interface declarations. This is a {@linkplain Node#isFindBoundary() find boundary}
 * for tree traversal methods.
 *
 * <pre class="grammar">
 *
 * ClassOrInterfaceDeclaration ::= {@link ASTModifierList ModifierList}
 *                                 ( "class" | "interface" )
 *                                 &lt;IDENTIFIER&gt;
 *                                 {@link ASTTypeParameters TypeParameters}?
 *                                 {@link ASTExtendsList ExtendsList}?
 *                                 {@link ASTImplementsList ImplementsList}?
 *                                 {@link ASTClassOrInterfaceBody ClassOrInterfaceBody}
 *
 * </pre>
 */
public final class ASTClassOrInterfaceDeclaration extends AbstractAnyTypeDeclaration {

    private boolean isInterface;

    ASTClassOrInterfaceDeclaration(int id) {
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

    @Override
    public boolean isInterface() {
        return this.isInterface;
    }

    void setInterface() {
        this.isInterface = true;
    }


    /**
     * Returns the superclass type node if this node is a class
     * declaration and explicitly declares an {@code extends}
     * clause. Superinterfaces of an interface are not considered.
     *
     * <p>Returns {@code null} otherwise.
     */
    public ASTClassOrInterfaceType getSuperClassTypeNode() {
        if (isInterface()) {
            return null;
        }

        ASTExtendsList extendsList = getFirstChildOfType(ASTExtendsList.class);
        return extendsList == null ? null : extendsList.iterator().next();
    }


    /**
     * Returns the interfaces implemented by this class, or
     * extended by this interface. Returns an empty list if
     * none is specified.
     */
    public List<ASTClassOrInterfaceType> getSuperInterfacesTypeNodes() {

        Iterable<ASTClassOrInterfaceType> it = isInterface()
                                               ? getFirstChildOfType(ASTExtendsList.class)
                                               : getFirstChildOfType(ASTImplementsList.class);

        return it == null ? Collections.emptyList() : IteratorUtil.toList(it.iterator());
    }

}
