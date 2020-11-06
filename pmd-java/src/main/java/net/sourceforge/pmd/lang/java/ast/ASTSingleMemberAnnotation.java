/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents an annotation using the shorthand syntax for the default member.
 *
 * <pre>
 *
 * SingleMemberAnnotation ::=  "@"  {@linkplain ASTName Name} "(" {@linkplain ASTMemberValue MemberValue} ")"
 *
 * </pre>
 *
 * @see ASTMarkerAnnotation
 * @see ASTNormalAnnotation
 */
public class ASTSingleMemberAnnotation extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTSingleMemberAnnotation(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTSingleMemberAnnotation(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the value of the default member
     * set by this annotation.
     */
    public ASTMemberValue getMemberValue() {
        return (ASTMemberValue) getChild(1);
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
