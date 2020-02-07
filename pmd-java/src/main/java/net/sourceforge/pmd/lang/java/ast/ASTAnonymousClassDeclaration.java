/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

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
    public boolean isFindBoundary() {
        return true;
    }


    @Override
    public Visibility getVisibility() {
        return Visibility.V_ANONYMOUS;
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }

}
