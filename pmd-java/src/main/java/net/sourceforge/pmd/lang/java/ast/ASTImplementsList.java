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
 * ImplementsList ::= "implements" {@link ASTClassType InterfaceType} ( "," {@link ASTClassType InterfaceType})*
 *
 * </pre>
 */
public final class ASTImplementsList extends ASTNonEmptyList<ASTClassType> {

    ASTImplementsList(int id) {
        super(id, ASTClassType.class);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
