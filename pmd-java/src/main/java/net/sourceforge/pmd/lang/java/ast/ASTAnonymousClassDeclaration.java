/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 * An anonymous class declaration. This can occur in a {@linkplain ASTConstructorCall class instance creation
 * expression}
 * or in an {@linkplain ASTEnumConstant enum constant declaration}.
 * This is a {@linkplain Node#isFindBoundary() find boundary} for tree traversal methods.
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
    public @NonNull NodeStream<ASTClassOrInterfaceType> getSuperInterfaceTypeNodes() {
        if (getParent() instanceof ASTConstructorCall) {
            ASTConstructorCall ctor = (ASTConstructorCall) getParent();
            @NonNull JTypeMirror type = ctor.getTypeMirror();
            if (type.isInterface()) {
                return NodeStream.of(ctor.getTypeNode());
            }
        }
        return NodeStream.empty();
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
