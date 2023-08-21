/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

public final class ASTDeclaration extends AbstractVfNode {

    private String name;

    ASTDeclaration(int id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    @Override
    protected <P, R> R acceptVfVisitor(VfVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
