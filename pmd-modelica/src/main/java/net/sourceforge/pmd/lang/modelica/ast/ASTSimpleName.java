/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

public final class ASTSimpleName extends AbstractModelicaNode {
    private String name;

    ASTSimpleName(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptModelicaVisitor(ModelicaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
