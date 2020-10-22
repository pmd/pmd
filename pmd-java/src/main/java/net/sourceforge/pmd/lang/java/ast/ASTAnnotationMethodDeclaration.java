/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * @deprecated Represented directly by {@link ASTMethodDeclaration MethodDeclaration}.
 *     An annotation method is just {@link ASTMethodDeclaration MethodDeclaration} whose
 *     enclosing type is an annotation.
 */
@Deprecated
public final class ASTAnnotationMethodDeclaration extends AbstractJavaNode implements AccessNode {

    ASTAnnotationMethodDeclaration(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        throw new UnsupportedOperationException("Node was removed from grammar");
    }

}
