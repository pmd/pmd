/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A class or instance initializer. Don't confuse with {@link ASTVariableInitializer}.
 *
 * <pre class="grammar">
 *
 * Initializer ::= "static"? {@link ASTBlock Block}
 *
 * </pre>
 *
 */
public final class ASTInitializer extends AbstractJavaNode {

    private boolean isStatic;

    ASTInitializer(int id) {
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


    public boolean isStatic() {
        return isStatic;
    }

    void setStatic() {
        isStatic = true;
    }
}
