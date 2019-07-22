/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A constructor of a {@linkplain ASTConstructorDeclaration class} or
 * {@linkplain ASTEnumDeclaration enum} declaration.
 *
 * <pre class="grammar">
 *
 * ConstructorDeclaration ::= ConstructorModifier*
 *                            {@link ASTTypeParameters TypeParameters}?
 *                            &lt;IDENTIFIER&gt;
 *                            {@link ASTFormalParameters FormalParameters}
 *                            ("throws" {@link ASTNameList NameList})?
 *                            {@link ASTBlock Block}
 *
 *
 * ConstructorModifier ::= "public" | "private"  | "protected"
 *                       | {@linkplain ASTAnnotation Annotation}
 *
 *
 * </pre>
 */
public final class ASTConstructorDeclaration extends AbstractMethodOrConstructorDeclaration {

    ASTConstructorDeclaration(int id) {
        super(id);
    }

    ASTConstructorDeclaration(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public MethodLikeKind getKind() {
        return MethodLikeKind.CONSTRUCTOR;
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    public boolean containsComment() {
        return getBody().containsComment();
    }

    @Override
    public @NonNull ASTBlock getBody() {
        return (ASTBlock) getLastChild();
    }

}
