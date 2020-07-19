/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


/**
 * Represents a single type argument in a {@linkplain ASTTypeArguments type arguments list}.
 *
 * <pre class="grammar">
 *
 * TypeArgument ::= ( {@linkplain ASTAnnotation Annotation} )* ( {@linkplain ASTReferenceType ReferenceType} | "?" {@linkplain ASTWildcardBounds WildcardBounds}? )
 *
 * </pre>
 *
 * @deprecated Replaced by just an {@link ASTType}
 */
@Deprecated
public final class ASTTypeArgument extends AbstractJavaTypeNode {

    ASTTypeArgument(int id) {
        super(id);
    }


    /**
     * Returns true if this node is a wildcard argument (bounded or not).
     */
    public boolean isWildcard() {
        return getTypeNode() == null;
    }


    /**
     * Returns the type node of this type argument.
     * Returns {@code null} if this is a wildcard argument.
     */
    public ASTReferenceType getTypeNode() {
        return getFirstChildOfType(ASTReferenceType.class);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
