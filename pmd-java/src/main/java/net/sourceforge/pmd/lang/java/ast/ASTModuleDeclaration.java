/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A module declaration in a {@linkplain ASTCompilationUnit modular compilation unit}.
 *
 * TODO(#2701): revisit module declarations
 */
public final class ASTModuleDeclaration extends AbstractJavaNode {

    private boolean open;

    ASTModuleDeclaration(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }
}
