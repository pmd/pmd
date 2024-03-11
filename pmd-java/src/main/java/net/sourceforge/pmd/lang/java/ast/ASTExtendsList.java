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
 * ExtendsList ::= "extends" {@link ASTClassType ClassType} ( "," {@link ASTClassType ClassType} )*
 * </pre>
 */
public final class ASTExtendsList extends ASTNonEmptyList<ASTClassType> {

    ASTExtendsList(int id) {
        super(id, ASTClassType.class);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
