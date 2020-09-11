/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.types.JTypeMirror;

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
    public @NonNull List<ASTClassOrInterfaceType> getSuperInterfaceTypeNodes() {
        if (getParent() instanceof ASTConstructorCall) {
            ASTConstructorCall ctor = (ASTConstructorCall) getParent();
            @NonNull JTypeMirror type = ctor.getTypeMirror();
            if (type.isInterface()) {
                return listOf(ctor.getTypeNode());
            }
        }
        return Collections.emptyList();
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
