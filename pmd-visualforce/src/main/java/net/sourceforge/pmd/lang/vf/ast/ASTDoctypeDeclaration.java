/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTDoctypeDeclaration extends AbstractVfNode {

    /**
     * Name of the document type. Cannot be null.
     */
    private String name;

    ASTDoctypeDeclaration(int id) {
        super(id);
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
