/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a type bound on a {@linkplain ASTTypeParameter type parameter}.
 * Type bounds specify the type of the type variable to which they apply as
 * an <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-4.html#jls-4.9">intersection type</a>.
 * The first bound type is a class or interface type, while the additional
 * bounds are necessarily interface types.
 *
 * <pre class="grammar">
 *
 * TypeBound ::= "extends" {@linkplain ASTAnnotation Annotation}* {@linkplain ASTType Type}
 *
 * </pre>
 *
 * @deprecated Since the annotations now nest inside the type, this is just replaced by the type node alone.
 */
@Deprecated
public final class ASTTypeBound extends AbstractJavaTypeNode {
    public ASTTypeBound(int id) {
        super(id);
    }


    /**
     * Returns a list with the type bounds of this node.
     * The returned list has at least one element.
     */
    public ASTType getTypeNode() {
        return getFirstChildOfType(ASTType.class);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        throw new UnsupportedOperationException("Node was removed from grammar");
    }
}
