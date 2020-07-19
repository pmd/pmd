/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A statement that contains a local class declaration. Note that this
 * is not a declaration itself.
 *
 * <pre class="grammar">
 *
 * LocalClassStatement ::= {@link ASTClassOrInterfaceDeclaration ClassDeclaration}
 *
 * </pre>
 */
public final class ASTLocalClassStatement extends AbstractStatement {

    ASTLocalClassStatement(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the contained declaration.
     */
    @NonNull
    public ASTClassOrInterfaceDeclaration getDeclaration() {
        return (ASTClassOrInterfaceDeclaration) getChild(0);
    }
}
