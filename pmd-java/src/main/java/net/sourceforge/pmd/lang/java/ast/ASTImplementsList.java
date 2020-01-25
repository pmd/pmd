/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.ast.ASTList.ASTNonEmptyList;


/**
 * Represents the {@code implements} clause of a class declaration.
 *
 * <pre class="grammar">
 *
 * ImplementsList ::= "implements" {@link ASTClassOrInterfaceType ClassOrInterfaceType} ( "," {@link ASTClassOrInterfaceType ClassOrInterfaceType})*
 *
 * </pre>
 */
public final class ASTImplementsList extends ASTNonEmptyList<ASTClassOrInterfaceType> {

    ASTImplementsList(int id) {
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
