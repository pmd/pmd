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
public final class ASTLocalClassStatement extends AbstractStatement implements LeftRecursiveNode {

    ASTLocalClassStatement(int id) {
        super(id);
    }

    ASTLocalClassStatement(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    /**
     * Returns the contained declaration.
     */
    @NonNull
    public ASTClassOrInterfaceDeclaration getDeclaration() {
        return (ASTClassOrInterfaceDeclaration) jjtGetChild(0);
    }
}
