/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;

/**
 * Represents class and interface declarations. This is a
 * {@linkplain Node#isFindBoundary() find boundary} for tree traversal methods.
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
     * @deprecated Use {@link #getPermitsClause()} or
     *             {@link JClassSymbol#getPermittedSubtypes()}
     */
    @Deprecated
    public List<ASTClassType> getPermittedSubclasses() {
        return ASTList.orEmpty(children(ASTPermitsList.class).first());
    }
}
