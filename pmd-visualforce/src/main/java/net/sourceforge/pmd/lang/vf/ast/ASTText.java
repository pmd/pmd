/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

public final class ASTText extends AbstractVfNode {

    ASTText(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVfVisitor(VfVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
