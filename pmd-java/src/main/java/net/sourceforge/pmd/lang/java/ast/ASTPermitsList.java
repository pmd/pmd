/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.ast.ASTList.ASTNonEmptyList;


/**
 * Represents the {@code permits} clause of a (sealed) class declaration.
 *
 * <p>This is a Java 17 Feature.
 *
 * <p>See https://openjdk.java.net/jeps/409
 *
 * <pre class="grammar">
 *
 *  PermitsList ::= "permits" ClassOrInterfaceType
 *                ( "," ClassOrInterfaceType )*
 * </pre>
 */
public final class ASTPermitsList extends ASTNonEmptyList<ASTClassOrInterfaceType> {

    ASTPermitsList(int id) {
        super(id, ASTClassOrInterfaceType.class);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
