/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity.ast;

import org.apache.commons.lang3.StringUtils;

public final class ASTBlock extends AbstractVtlNode {

    ASTBlock(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVtlVisitor(VtlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public boolean isEmpty() {
        return getNumChildren() == 0
            || getNumChildren() == 1
            && getChild(0) instanceof ASTText
            && StringUtils.isBlank(getChild(0).getText());
    }
}
