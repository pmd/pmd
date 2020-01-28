/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.ast.MethodLikeNode.MethodLikeKind;

/**
 * @deprecated Represented directly by {@link ASTMethodDeclaration MethodDeclaration}.
 *     An annotation method is just {@link ASTMethodDeclaration MethodDeclaration} whose
 *     enclosing type is an annotation.
 */
@Deprecated
public final class ASTAnnotationMethodDeclaration extends AbstractJavaAccessNode {

    ASTAnnotationMethodDeclaration(int id) {
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

    public MethodLikeKind getKind() {
        return MethodLikeKind.METHOD;
    }
}
