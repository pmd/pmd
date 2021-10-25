/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Represents an enum declaration.
 * This is a {@linkplain Node#isFindBoundary() find boundary} for tree traversal methods.
 *
 * <p>An enum declaration is implicitly final <i>unless it contains at
 * least one enum constant that has a class body</i>. A nested enum type
 * is implicitly static.
 *
 * <pre class="grammar">
 *
 * EnumDeclaration ::= {@link ASTModifierList ModifierList}
 *                     "enum"
 *                     &lt;IDENTIFIER&gt;
 *                     {@linkplain ASTImplementsList ImplementsList}?
 *                     {@link ASTEnumBody EnumBody}
 *
 * </pre>
 */
public final class ASTEnumDeclaration extends AbstractAnyTypeDeclaration {


    ASTEnumDeclaration(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    @Override
    public ASTEnumBody getBody() {
        return (ASTEnumBody) getLastChild();
    }
}
