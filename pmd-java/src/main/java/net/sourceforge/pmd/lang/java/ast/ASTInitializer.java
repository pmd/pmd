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
public final class ASTInitializer extends AbstractJavaNode implements ASTBodyDeclaration {

    private boolean isStatic;

    ASTInitializer(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    public boolean isStatic() {
        return isStatic;
    }

    void setStatic() {
        isStatic = true;
    }
}
