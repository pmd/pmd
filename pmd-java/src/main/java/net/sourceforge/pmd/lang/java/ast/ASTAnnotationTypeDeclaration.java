/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * The declaration of an annotation type.
 * This is a {@linkplain Node#isFindBoundary() find boundary} for tree traversal methods.
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
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    @Override
    public boolean isInterface() {
        return true;
    }
}
