/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a type parameter declaration of a method, constructor, class or interface declaration.
 *
 * <pre>
 *
 * TypeParameter ::= {@linkplain ASTAnnotation Annotation}* &lt;IDENTIFIER&gt; {@linkplain ASTTypeBound TypeBound}?
 *
 * </pre>
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-4.html#jls-4.4">JLS</a>
 */
// TODO should implement Annotatable when we use can use Java 8 mixins instead of an abstract class
public class ASTTypeParameter extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTTypeParameter(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTTypeParameter(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * Returns the name of the type variable introduced by this declaration.
     */
    public String getParameterName() {
        return getImage();
    }


    /**
     * Returns true if this type parameter is bounded,
     * in which case {@link #getTypeBoundNode()} doesn't
     * return {@code null}.
     */
    public final boolean hasTypeBound() {
        return getTypeBoundNode() != null;
    }


    /**
     * Returns the type bound node of this parameter,
     * or null if it is not bounded.
     */
    public final ASTTypeBound getTypeBoundNode() {
        return getFirstChildOfType(ASTTypeBound.class);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
