/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents an annotation with no declared member, e.g. {@code @Override}.
 *
 * <pre class="grammar">
 *
 * MarkerAnnotation ::= "@" Name
 *
 * </pre>
 *
 * @see ASTSingleMemberAnnotation
 * @see ASTNormalAnnotation
 */
public final class ASTMarkerAnnotation extends AbstractJavaTypeNode implements ASTAnnotation {

    ASTMarkerAnnotation(int id) {
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


}
