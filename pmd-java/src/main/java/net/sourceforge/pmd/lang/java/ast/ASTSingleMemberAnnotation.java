/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents an annotation using the shorthand syntax for the default member.
 *
 * <pre class="grammar">
 *
 * SingleMemberAnnotation ::=  "@" Name "(" {@linkplain ASTMemberValue MemberValue} ")"
 *
 * </pre>
 *
 * @see ASTMarkerAnnotation
 * @see ASTNormalAnnotation
 */
public final class ASTSingleMemberAnnotation extends AbstractJavaTypeNode implements ASTAnnotation {
    ASTSingleMemberAnnotation(int id) {
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


    /**
     * Returns the value of the default member
     * set by this annotation.
     */
    public ASTMemberValue getMemberValue() {
        return (ASTMemberValue) getChild(0);
    }

}
