/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents an annotation that with a parenthesized list
 * of key-value pairs (possibly empty).
 *
 * <pre>
 *
 * NormalAnnotation ::=  "@" {@linkplain ASTName Name} "(" {@linkplain ASTMemberValuePairs MemberValuePairs}? ")"
 *
 * </pre>
 *
 * @see ASTSingleMemberAnnotation
 * @see ASTMarkerAnnotation
 */
public class ASTNormalAnnotation extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTNormalAnnotation(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the name of the annotation as it is used,
     * eg {@code java.lang.Override} or {@code Override}.
     */
    public String getAnnotationName() {
        return getChild(0).getImage();
    }


    @Override
    public ASTAnnotation getParent() {
        return (ASTAnnotation) super.getParent();
    }
}
