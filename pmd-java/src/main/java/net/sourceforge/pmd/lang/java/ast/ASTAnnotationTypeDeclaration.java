/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * The declaration of an annotation type.
 *
 * <p>Note that in contrast to interface types, no {@linkplain ASTExtendsList extends clause}
 * is permitted, and an annotation type cannot be generic.
 *
 * <pre class="grammar">
 *
 * AnnotationTypeDeclaration ::= {@link ASTModifierList ModifierList}
 *                               "@" "interface"
 *                               &lt;IDENTIFIER&gt;
 *                               {@link ASTAnnotationTypeBody AnnotationTypeBody}
 *
 * </pre>
 *
 */
public final class ASTAnnotationTypeDeclaration extends AbstractAnyTypeDeclaration {


    ASTAnnotationTypeDeclaration(int id) {
        super(id);
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
    public boolean isInterface() {
        return true;
    }

}
