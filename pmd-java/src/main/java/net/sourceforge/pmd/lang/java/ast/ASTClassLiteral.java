/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A class literal. Class literals are {@linkplain ASTPrimaryExpression primary expressions},
 * but not proper {@linkplain ASTLiteral literals}, since they are represented by several tokens.
 *
 * <pre class="grammar">
 *
 * ClassLiteral ::= ({@link ASTType Type} | "void") "." "class"
 *
 * </pre>
 */
public final class ASTClassLiteral extends AbstractJavaExpr implements ASTPrimaryExpression {
    ASTClassLiteral(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    public boolean isVoid() {
        return getNumChildren() == 0;
    }


    /**
     * Returns the enclosed type node, or an empty optional if this is void.
     */
    @Nullable
    public ASTType getTypeNode() {
        return isVoid() ? null : (ASTType) getChild(0);
    }
}
