/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * Represents class and interface declarations.
 * This is a {@linkplain Node#isFindBoundary() find boundary} for tree traversal methods.
 *
 * <pre class="grammar">
 *
 * ClassDeclaration ::= {@link ASTModifierList ModifierList}
 *                      ( "class" | "interface" )
 *                      &lt;IDENTIFIER&gt;
 *                      {@link ASTTypeParameters TypeParameters}?
 *                      {@link ASTExtendsList ExtendsList}?
 *                      {@link ASTImplementsList ImplementsList}?
 *                      {@link ASTClassBody ClassBody}
 *
 * </pre>
 */
public final class ASTClassDeclaration extends AbstractTypeDeclaration {

    private boolean isInterface;

    ASTClassDeclaration(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public boolean isInterface() {
        return this.isInterface;
    }

    @Override
    public boolean isRegularClass() {
        return !isInterface;
    }

    @Override
    public boolean isRegularInterface() {
        return isInterface;
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
    public ASTClassType getSuperClassTypeNode() {
        if (isInterface()) {
            return null;
        }

        ASTExtendsList extendsList = firstChild(ASTExtendsList.class);
        return extendsList == null ? null : extendsList.iterator().next();
    }


    public List<ASTClassType> getPermittedSubclasses() {
        return ASTList.orEmpty(children(ASTPermitsList.class).first());
    }
}
