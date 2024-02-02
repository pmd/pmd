/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

public final class ASTWithinClause extends AbstractModelicaNode {
    ASTWithinClause(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptModelicaVisitor(ModelicaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getName() {
        ASTName name = firstChild(ASTName.class);
        if (name != null) {
            return name.getName();
        }
        return "";
    }
}
