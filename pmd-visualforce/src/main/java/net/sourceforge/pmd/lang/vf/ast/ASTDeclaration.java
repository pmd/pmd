/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTDeclaration extends AbstractVFNode {

    private String name;

    @Deprecated
    @InternalApi
    public ASTDeclaration(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTDeclaration(VfParser p, int id) {
        super(p, id);
    }

    public String getName() {
        return name;
    }

    @Deprecated
    @InternalApi
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object jjtAccept(VfParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
