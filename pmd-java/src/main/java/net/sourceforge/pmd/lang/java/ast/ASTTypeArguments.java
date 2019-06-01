/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;


/**
 * Represents a list of type arguments. This is different from {@linkplain ASTTypeParameters type parameters}!
 *
 * <pre class="grammar">
 *
 *  TypeArguments ::= "&lt;" ({@link ASTAnnotation TypeAnnotation})* {@linkplain ASTReferenceType TypeArgument} ( "," ({@link ASTAnnotation TypeAnnotation})* {@linkplain ASTReferenceType TypeArgument} )* "&gt;"
 *                  | "&lt;" "&gt;"
 * </pre>
 */
public final class ASTTypeArguments extends AbstractJavaNode implements Iterable<ASTType> {

    ASTTypeArguments(int id) {
        super(id);
    }

    ASTTypeArguments(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    /**
     * Returns true if this is a diamond, that is, the
     * actual type arguments are inferred.
     */
    public boolean isDiamond() {
        return jjtGetNumChildren() == 0;
    }


    @Override
    public Iterator<ASTType> iterator() {
        return new NodeChildrenIterator<>(this, ASTType.class);
    }
}
