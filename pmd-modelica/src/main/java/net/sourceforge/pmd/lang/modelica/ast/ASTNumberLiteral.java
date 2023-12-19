/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

public final class ASTNumberLiteral extends AbstractModelicaNode {
    private String value;

    ASTNumberLiteral(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptModelicaVisitor(ModelicaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
