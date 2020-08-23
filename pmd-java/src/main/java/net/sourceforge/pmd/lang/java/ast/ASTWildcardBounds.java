/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a type bound on a wildcard {@linkplain ASTTypeArgument type argument}.
 *
 * <pre class="grammar">
 *
 * WildcardBounds ::=  ( "extends" | "super" ) ( {@linkplain ASTAnnotation Annotation} )* {@linkplain ASTReferenceType ReferenceType}
 *
 * </pre>
 * @deprecated Replaced by {@link ASTWildcardType}
 */
@Deprecated
public final class ASTWildcardBounds extends AbstractJavaTypeNode {

    private boolean isUpperBound;

    ASTWildcardBounds(int id) {
        super(id);
    }


    /**
     * Returns true if this is an upper type bound, e.g.
     * in {@code <? extends Integer>}.
     */
    public boolean isUpperBound() {
        return isUpperBound;
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
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        throw new UnsupportedOperationException("Node was removed from grammar");
    }
}
