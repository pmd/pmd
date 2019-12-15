/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Return type of a method. TODO remove, the method declaration could have no type node if it's void
 *
 * <pre class="grammar">
 *
 * ResultType ::= "void" | {@link ASTType Type}
 *
 * </pre>
 */
public final class ASTResultType extends AbstractJavaNode {

    ASTResultType(int id) {
        super(id);
    }

    ASTResultType(JavaParser p, int id) {
        super(p, id);
    }

    public boolean returnsArray() {
        return !isVoid() && ((ASTType) jjtGetChild(0)).isArrayType();
    }

    public boolean isVoid() {
        return jjtGetNumChildren() == 0;
    }


    /**
     * Returns the enclosed type node, or an null if this is void.
     */
    @Nullable
    public ASTType getTypeNode() {
        return isVoid() ? null : (ASTType) jjtGetChild(0);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }
}
