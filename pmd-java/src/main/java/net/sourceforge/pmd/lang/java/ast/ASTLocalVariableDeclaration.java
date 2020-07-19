/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a local variable declaration. This is a {@linkplain ASTStatement statement},
 * but the node is also used in {@linkplain ASTForInit for-loop initialisers} and
 * {@linkplain ASTForStatement foreach statements}.
 *
 * <p>This statement may define several variables, possibly of different types
 * (see {@link ASTVariableDeclaratorId#getType()}). The nodes corresponding to
 * the declared variables are accessible through {@link #getVarIds()}.
 *
 * <pre class="grammar">
 *
 * LocalVariableDeclaration ::= {@link ASTModifierList LocalVarModifierList} {@linkplain ASTType Type} {@linkplain ASTVariableDeclarator VariableDeclarator} ( "," {@linkplain ASTVariableDeclarator VariableDeclarator} )*
 *
 * </pre>
 */
// TODO extend AbstractStatement
public final class ASTLocalVariableDeclaration extends AbstractJavaNode
    implements Iterable<ASTVariableDeclaratorId>,
               ASTStatement,
               FinalizableNode,
               InternalInterfaces.MultiVariableIdOwner {

    ASTLocalVariableDeclaration(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public Visibility getVisibility() {
        return Visibility.V_LOCAL;
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

    /**
     * Gets the type node for this variable declaration statement.
     * With Java10 and local variable type inference, there might be
     * no type node at all.
     *
     * @return The type node or <code>null</code>
     *
     * @see #isTypeInferred()
     */
    @Override
    public ASTType getTypeNode() {
        return getFirstChildOfType(ASTType.class);
    }


}
