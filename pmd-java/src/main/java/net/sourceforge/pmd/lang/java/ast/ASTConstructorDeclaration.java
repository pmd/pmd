/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;

/**
 * A constructor of a {@linkplain ASTConstructorDeclaration class} or
 * {@linkplain ASTEnumDeclaration enum} declaration.
 *
 * <pre class="grammar">
 *
 * ConstructorDeclaration ::= {@link ASTModifierList ModifierList}
 *                            {@link ASTTypeParameters TypeParameters}?
 *                            &lt;IDENTIFIER&gt;
 *                            {@link ASTFormalParameters FormalParameters}
 *                            ({@link ASTThrowsList ThrowsList})?
 *                            {@link ASTBlock Block}
 *
 * </pre>
 */
public final class ASTConstructorDeclaration extends AbstractExecutableDeclaration<JConstructorSymbol> {

    ASTConstructorDeclaration(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    @Override
    public String getImage() {
        return getName();
    }


    public boolean containsComment() {
        return getBody().containsComment();
    }

    @Override
    public @NonNull ASTBlock getBody() {
        return (ASTBlock) getLastChild();
    }

}
