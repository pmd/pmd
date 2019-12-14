/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;


/**
 * Represents the {@code extends} clause of a class or interface declaration.
 * If the parent is an interface declaration, then these types are all interface
 * types. Otherwise, then this list contains exactly one element.
 *
 * <pre class="grammar">
 *
 * ExtendsList ::= "extends" {@link ASTType Type} ( "," {@link ASTType Type} )*
 * </pre>
 */
public final class ASTExtendsList extends AbstractJavaNode implements Iterable<ASTClassOrInterfaceType> {

    ASTExtendsList(int id) {
        super(id);
    }

    ASTExtendsList(JavaParser p, int id) {
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


    @Override    // TODO this doesn't preserve the annotations.
    public Iterator<ASTClassOrInterfaceType> iterator() {
        return children(ASTClassOrInterfaceType.class).iterator();
    }
}
