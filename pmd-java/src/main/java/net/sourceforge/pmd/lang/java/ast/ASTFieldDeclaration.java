/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;


/**
 * Represents a field declaration in the body of a type declaration.
 *
 * <p>This declaration may define several variables, possibly of different
 * types. The nodes corresponding to the declared variables are accessible
 * through {@link #iterator()}.
 *
 * <pre class="grammar">
 *
 * FieldDeclaration ::= {@link ASTModifierList ModifierList} {@linkplain ASTType Type} {@linkplain ASTVariableDeclarator VariableDeclarator} ( "," {@linkplain ASTVariableDeclarator VariableDeclarator} )* ";"
 *
 * </pre>
 */
public final class ASTFieldDeclaration extends AbstractJavaNode
    implements Iterable<ASTVariableDeclaratorId>,
               LeftRecursiveNode,
               AccessNode,
               ASTBodyDeclaration,
               InternalInterfaces.MultiVariableIdOwner,
               JavadocCommentOwner {


    ASTFieldDeclaration(int id) {
        super(id);
    }

    @Override
    protected @Nullable JavaccToken getPreferredReportLocation() {
        // report on the identifier and not the annotations
        return getVarIds().firstOrThrow().getFirstToken();
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the type node at the beginning of this field declaration.
     * The type of this node is not necessarily the type of the variables,
     * query the type of individual {@link ASTVariableDeclaratorId}.
     */
    @Override
    public ASTType getTypeNode() {
        return firstChild(ASTType.class);
    }

}
