/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
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
public final class ASTConstructorDeclaration extends AbstractMethodOrConstructorDeclaration<JConstructorSymbol> {

    ASTConstructorDeclaration(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return getImage();
    }

    @Override
    protected @Nullable JavaccToken getPreferredReportLocation() {
        return getModifiers().getLastToken().getNext();
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    public boolean containsComment() {
        return getBody().containsComment();
    }

    @Override
    public @NonNull ASTBlock getBody() {
        return (ASTBlock) getLastChild();
    }

}
