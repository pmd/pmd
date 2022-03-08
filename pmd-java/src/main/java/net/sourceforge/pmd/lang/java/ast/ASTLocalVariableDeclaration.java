/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

/**
 * Represents a local variable declaration. This is a {@linkplain ASTStatement statement},
 * but the node is also used in {@linkplain ASTForInit for-loop initialisers} and
 * {@linkplain ASTForStatement foreach statements}.
 *
 * <p>This statement may define several variables, possibly of different types.
 * The nodes corresponding to the declared variables are accessible
 * through {@link #getVarIds()}.
 *
 * <pre class="grammar">
 *
 * LocalVariableDeclaration ::= {@link ASTModifierList LocalVarModifierList} {@linkplain ASTType Type} {@linkplain ASTVariableDeclarator VariableDeclarator} ( "," {@linkplain ASTVariableDeclarator VariableDeclarator} )*
 *
 * </pre>
 */
public final class ASTLocalVariableDeclaration extends AbstractStatement
    implements Iterable<ASTVariableDeclaratorId>,
               ASTStatement,
               InternalInterfaces.FinalizableNode,
               LeftRecursiveNode, // ModifierList is parsed separately in BlockStatement
               InternalInterfaces.MultiVariableIdOwner {

    ASTLocalVariableDeclaration(int id) {
        super(id);
    }

    @Override
    protected @Nullable JavaccToken getPreferredReportLocation() {
        return getVarIds().firstOrThrow().getFirstToken();
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * If true, this local variable declaration represents a declaration,
     * which makes use of local variable type inference, e.g. java10 "var".
     * You can receive the inferred type via {@link ASTVariableDeclarator#getTypeMirror()}.
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
