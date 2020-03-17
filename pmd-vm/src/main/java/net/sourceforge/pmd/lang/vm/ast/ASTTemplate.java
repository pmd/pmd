/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.ast;

import net.sourceforge.pmd.lang.ast.RootNode;

public final class ASTTemplate extends AbstractVmNode implements RootNode {

    public ASTTemplate(int id) {
        super(id);
    }


    @Override
    public Object jjtAccept(VmParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
