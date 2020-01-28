/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a single member-value pair in a {@linkplain ASTNormalAnnotation NormalAnnotation}.
 *
 * <pre class="grammar">
 *
 * MemberValuePair ::=  &lt;IDENTIFIER&gt; "=" {@linkplain ASTMemberValue MemberValue}
 *
 * </pre>
 */
public final class ASTMemberValuePair extends AbstractJavaNode {
    ASTMemberValuePair(int id) {
        super(id);
    }

    /**
     * Returns the name of the member set by this pair.
     */
    public String getMemberName() {
        return getImage();
    }


    /**
     * Returns the value of the member set by this pair.
     */
    public ASTMemberValue getMemberValue() {
        return (ASTMemberValue) getChild(0);
    }


    @Override
    public ASTNormalAnnotation getParent() {
        return (ASTNormalAnnotation) super.getParent();
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
