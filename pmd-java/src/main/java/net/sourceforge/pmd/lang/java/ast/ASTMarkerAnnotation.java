/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents an annotation with no declared member, e.g. {@code @Override}.
 *
 * <pre>
 *
 * MarkerAnnotation ::= "@" {@linkplain ASTAnnotation Name}
 *
 * </pre>
 *
 * @see ASTSingleMemberAnnotation
 * @see ASTNormalAnnotation
 */
public class ASTMarkerAnnotation extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTMarkerAnnotation(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTMarkerAnnotation(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
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
