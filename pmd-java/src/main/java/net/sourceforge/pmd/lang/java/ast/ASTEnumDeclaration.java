/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Represents an enum declaration. This is a {@linkplain Node#isFindBoundary() find boundary}
 * for tree traversal methods.
 *
 * <p>An enum declaration is implicitly final <i>unless it contains at
 * least one enum constant that has a class body</i>. A nested enum type
 * is implicitly static.
 *
 * <pre class="grammar">
 *
 * EnumDeclaration ::= EnumModifier*
 *                     "enum"
 *                     &lt;IDENTIFIER&gt;
 *                     {@linkplain ASTImplementsList ImplementsList}?
 *                     {@link ASTEnumBody EnumBody}
 *
 *
 *
 * EnumModifier ::= "public" | "private"  | "protected"
 *                | "strictfp" | "static"
 *                | {@linkplain ASTAnnotation Annotation}
 *
 * </pre>
 */
public final class ASTEnumDeclaration extends AbstractAnyTypeDeclaration {


    ASTEnumDeclaration(int id) {
        super(id);
    }

    ASTEnumDeclaration(JavaParser p, int id) {
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


    @Override
    public TypeKind getTypeKind() {
        return TypeKind.ENUM;
    }

    /**
     * Returns the enum constants declared by this enum.
     */
    public List<ASTEnumConstant> getConstants() {
        return getFirstChildOfType(ASTEnumBody.class).findChildrenOfType(ASTEnumConstant.class);
    }

    @Override
    public List<ASTAnyTypeBodyDeclaration> getDeclarations() {
        return getFirstChildOfType(ASTEnumBody.class)
            .findChildrenOfType(ASTAnyTypeBodyDeclaration.class);
    }
}
