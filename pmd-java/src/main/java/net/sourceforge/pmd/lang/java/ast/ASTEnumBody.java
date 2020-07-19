/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Body of an {@linkplain ASTEnumDeclaration enum declaration}.
 *
 * <pre class="grammar">
 *
 * EnumBody ::= "{"
 *              [ {@link ASTEnumConstant EnumConstant} ( "," ( {@link ASTEnumConstant EnumConstant} )* ]
 *              [ "," ]
 *              [ ";" ( {@link ASTBodyDeclaration ClassOrInterfaceBodyDeclaration} )* ]
 *              "}"
 *
 * </pre>
 *
 *
 */
public final class ASTEnumBody extends ASTTypeBody {

    private boolean trailingComma;
    private boolean separatorSemi;

    ASTEnumBody(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    void setTrailingComma() {
        this.trailingComma = true;
    }

    void setSeparatorSemi() {
        this.separatorSemi = true;
    }

    /**
     * Returns true if the last enum constant has a trailing comma.
     * For example:
     * <pre>{@code
     * enum Foo { A, B, C, }
     * enum Bar { , }
     * }</pre>
     */
    public boolean hasTrailingComma() {
        return trailingComma;
    }

    /**
     * Returns true if the last enum constant has a trailing semi-colon.
     * This semi is not optional when the enum has other members.
     * For example:
     * <pre>{@code
     * enum Foo {
     *   A(2);
     *
     *   Foo(int i) {...}
     * }
     *
     * enum Bar { A; }
     * enum Baz { ; }
     * }</pre>
     */
    public boolean hasSeparatorSemi() {
        return separatorSemi;
    }
}
