/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;


/**
 * Represents class and interface declarations. This is a {@linkplain Node#isFindBoundary() find boundary}
 * for tree traversal methods.
 *
 * <pre class="grammar">
 *
 * ClassOrInterfaceDeclaration ::= ( "class" | "interface" )
 *                                 &lt;IDENTIFIER&gt;
 *                                 {@linkplain ASTTypeParameters TypeParameters}?
 *                                 {@linkplain ASTExtendsList ExtendsList}?
 *                                 {@linkplain ASTImplementsList ImplementsList}?
 *                                 {@linkplain ASTClassOrInterfaceBody ClassOrInterfaceBody}
 * </pre>
 */
public class ASTClassOrInterfaceDeclaration extends AbstractAnyTypeDeclaration {

    private boolean isInterface;

    @InternalApi
    @Deprecated
    public ASTClassOrInterfaceDeclaration(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public boolean isPackagePrivate() {
        return super.isPackagePrivate() && !isLocal();
    }

    public boolean isInterface() {
        return this.isInterface;
    }

    @InternalApi
    @Deprecated
    public void setInterface() {
        this.isInterface = true;
    }

    @Override
    public TypeKind getTypeKind() {
        return isInterface() ? TypeKind.INTERFACE : TypeKind.CLASS;
    }


    @Override
    public NodeStream<ASTAnyTypeBodyDeclaration> getDeclarations() {
        return children(ASTClassOrInterfaceBody.class).children(ASTAnyTypeBodyDeclaration.class);
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

    @Experimental
    public List<ASTClassOrInterfaceType> getPermittedSubclasses() {
        ASTPermitsList permitted = getFirstChildOfType(ASTPermitsList.class);
        return permitted == null
                ? Collections.emptyList()
                : IteratorUtil.toList(permitted.iterator());
    }

    @Experimental
    public boolean isSealed() {
        int modifiers = getModifiers();
        return (modifiers & AccessNode.SEALED) == AccessNode.SEALED;
    }

    @Experimental
    public boolean isNonSealed() {
        int modifiers = getModifiers();
        return (modifiers & AccessNode.NON_SEALED) == AccessNode.NON_SEALED;
    }
}
