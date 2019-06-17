/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A class literal.
 *
 * <pre class="grammar">
 *
 * ClassLiteral ::= ({@link ASTType Type} | "void") "." "class"
 *
 * </pre>
 */
public final class ASTClassLiteral extends AbstractLiteral implements ASTLiteral, LeftRecursiveNode {
    ASTClassLiteral(int id) {
        super(id);
    }


    ASTClassLiteral(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * Accept the visitor. *
     */
    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    public boolean isVoid() {
        return jjtGetNumChildren() == 0;
    }


    /**
     * Returns the enclosed type node, or an empty optional if this is void.
     */
    @Nullable
    public ASTType getTypeNode() {
        return isVoid() ? null : (ASTType) jjtGetChild(0);
    }
}
