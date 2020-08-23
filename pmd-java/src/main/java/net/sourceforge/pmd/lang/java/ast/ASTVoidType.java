/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Type node to represent the void pseudo-type. This represents the
 * absence of a type, not a type, but it's easier to process that way.
 * Can only occur as return type of method declarations, and as the qualifier
 * of a {@linkplain ASTClassLiteral class literal}.
 *
 * <pre class="grammar">
 *
 * VoidType ::= "void"
 *
 * </pre>
 */
public final class ASTVoidType extends AbstractJavaTypeNode implements ASTType {

    ASTVoidType(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    @Deprecated
    public String getTypeImage() {
        return "void";
    }
}
