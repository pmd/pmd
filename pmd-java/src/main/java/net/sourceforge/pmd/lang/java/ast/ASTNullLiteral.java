/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * The null literal.
 *
 * <pre class="grammar">
 *
 * NullLiteral ::= "null"
 *
 * </pre>
 */
public final class ASTNullLiteral extends AbstractLiteral implements ASTLiteral {
    ASTNullLiteral(int id) {
        super(id);
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
