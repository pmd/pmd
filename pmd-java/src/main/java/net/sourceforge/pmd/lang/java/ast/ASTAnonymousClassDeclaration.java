/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An anonymous class declaration. This can occur in a {@linkplain ASTConstructorCall class instance creation
 * expression}
 * or in an {@linkplain ASTEnumConstant enum constant declaration}.
 *
 *
 * <pre class="grammar">
 *
 * AnonymousClassDeclaration ::= {@link ASTModifierList EmptyModifierList} {@link ASTClassOrInterfaceBody}
 *
 * </pre>
 */
public final class ASTAnonymousClassDeclaration extends AbstractAnyTypeDeclaration {


    ASTAnonymousClassDeclaration(int id) {
        super(id);
    }


    @Override
    public @NonNull String getSimpleName() {
        return "";
    }

    @Override
    public boolean isFindBoundary() {
        return true;
    }


    @Override
    public Visibility getVisibility() {
        return Visibility.V_ANONYMOUS;
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
