/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.ast.ASTList.ASTNonEmptyList;


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
public final class ASTExtendsList extends ASTNonEmptyList<ASTClassOrInterfaceType> {

    ASTExtendsList(int id) {
        super(id, ASTClassOrInterfaceType.class);
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
