/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a type bound on a wildcard {@linkplain ASTTypeArgument type argument}.
 *
 * <pre>
 *
 * WildcardBounds ::=  ( "extends" | "super" ) ( {@linkplain ASTAnnotation Annotation} )* {@linkplain ASTReferenceType ReferenceType}
 *
 * </pre>
 */
public class ASTWildcardBounds extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTWildcardBounds(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTWildcardBounds(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * Returns true if this is an upper type bound, e.g.
     * in {@code <? extends Integer>}.
     */
    public boolean isUpperBound() {
        return jjtGetFirstToken().toString().equals("extends");
    }


    /**
     * Returns true if this is a lower type bound, e.g.
     * in {@code <? super Node>}.
     */
    public boolean isLowerBound() {
        return !isUpperBound();
    }


    /**
     * Returns the type node representing the bound, e.g.
     * the {@code Node} in {@code <? super Node>}.
     */
    public ASTReferenceType getTypeBoundNode() {
        return getFirstChildOfType(ASTReferenceType.class);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
