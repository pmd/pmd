/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import net.sourceforge.pmd.annotation.InternalApi;

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
public class ASTTypeArgument extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTTypeArgument(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTTypeArgument(JavaParser p, int id) {
        super(p, id);
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
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }
}
