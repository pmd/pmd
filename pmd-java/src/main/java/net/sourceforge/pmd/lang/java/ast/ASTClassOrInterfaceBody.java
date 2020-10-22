/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents the body of a {@linkplain ASTClassOrInterfaceDeclaration class or interface declaration}.
 * This includes anonymous classes, including those defined within an {@linkplain ASTEnumConstant enum constant}.
 *
 * <pre class="grammar">
 *
 * ClassOrInterfaceBody ::=  "{"  {@linkplain ASTBodyDeclaration ClassOrInterfaceBodyDeclaration}* "}"
 *
 * </pre>
 */
public final class ASTClassOrInterfaceBody extends ASTTypeBody {

    ASTClassOrInterfaceBody(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
