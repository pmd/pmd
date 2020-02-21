/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.ast;

import org.apache.commons.lang3.StringUtils;

public final class ASTBlock extends AbstractVmNode {

    ASTBlock(int id) {
        super(id);
    }


    @Override
    public Object jjtAccept(VmParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean isEmpty() {
        return getNumChildren() == 0
            || getNumChildren() == 1
            && getChild(0) instanceof ASTText
            && StringUtils.isBlank(getChild(0).getText());
    }
}
