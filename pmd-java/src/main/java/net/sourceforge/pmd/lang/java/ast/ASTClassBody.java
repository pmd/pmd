/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents the body of a {@linkplain ASTClassDeclaration class or interface declaration}.
 * This includes anonymous classes, including those defined within an {@linkplain ASTEnumConstant enum constant}.
 *
 * <pre class="grammar">
 *
 * ClassBody ::=  "{"  {@linkplain ASTBodyDeclaration ClassBodyDeclaration}* "}"
 *
 * </pre>
 */
public final class ASTClassBody extends ASTTypeBody {

    ASTClassBody(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
