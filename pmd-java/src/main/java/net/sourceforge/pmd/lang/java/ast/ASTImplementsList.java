/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;


/**
 * Represents the {@code implements} clause of a class declaration.
 *
 * <pre class="grammar">
 *  ExtendsList ::= "implements" (TypeAnnotation)* ClassOrInterfaceType
 *                ( "," (TypeAnnotation)* ClassOrInterfaceType )*
 * </pre>
 */
public final class ASTImplementsList extends AbstractJavaNode implements Iterable<ASTClassOrInterfaceType> {

    ASTImplementsList(int id) {
        super(id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    @Override
    public Iterator<ASTClassOrInterfaceType> iterator() {
        return children(ASTClassOrInterfaceType.class).iterator();
    }
}
