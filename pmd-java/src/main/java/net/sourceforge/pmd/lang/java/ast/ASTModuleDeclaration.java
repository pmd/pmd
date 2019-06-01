/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTModuleDeclaration extends AbstractJavaNode {

    private boolean open;

    @InternalApi
    @Deprecated
    public ASTModuleDeclaration(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTModuleDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @InternalApi
    @Deprecated
    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }
}
