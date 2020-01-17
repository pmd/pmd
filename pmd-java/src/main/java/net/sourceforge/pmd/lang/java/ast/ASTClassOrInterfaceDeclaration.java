/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.CollectionUtil;


/**
 * Represents class and interface declarations. This is a {@linkplain Node#isFindBoundary() find boundary}
 * for tree traversal methods.
 *
 * <pre>
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

    private boolean isLocal;
    private boolean isLocalComputed; // guard for lazy evaluation of isLocal()

    private boolean isInterface;

    @InternalApi
    @Deprecated
    public ASTClassOrInterfaceDeclaration(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTClassOrInterfaceDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public boolean isFindBoundary() {
        return isNested() || isLocal();
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public boolean isPackagePrivate() {
        return super.isPackagePrivate() && !isLocal();
    }

    /**
     * Returns true if the class is declared inside a block other
     * than the body of another class, or the top level.
     */
    public boolean isLocal() {
        if (!isLocalComputed) {
            Node current = getParent();
            while (current != null) {
                if (current instanceof ASTAnyTypeDeclaration) {
                    isLocal = false;
                    break;
                } else if (current instanceof ASTMethodOrConstructorDeclaration
                    || current instanceof ASTInitializer) {
                    isLocal = true;
                    break;
                }
                current = current.getParent();
            }
            if (current == null) {
                isLocal = false;
            }
            isLocalComputed = true;
        }
        return isLocal;
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
    public List<ASTAnyTypeBodyDeclaration> getDeclarations() {
        return getFirstChildOfType(ASTClassOrInterfaceBody.class)
            .findChildrenOfType(ASTAnyTypeBodyDeclaration.class);
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

        return it == null ? Collections.<ASTClassOrInterfaceType>emptyList() : CollectionUtil.toList(it.iterator());
    }

}
