/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import net.sourceforge.pmd.lang.java.ast.ASTList.ASTNonEmptyList;


/**
 * Represents a list of type parameters.
 *
 * <pre class="grammar">
 *
 * TypeParameters ::= "<" {@linkplain ASTTypeParameter TypeParameter} ( "," {@linkplain ASTTypeParameter TypeParameter} )* ">"
 *
 * </pre>
 */
public final class ASTTypeParameters extends ASTNonEmptyList<ASTTypeParameter> {

    ASTTypeParameters(int id) {
        super(id, ASTTypeParameter.class);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
