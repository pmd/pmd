/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

public final class ASTWithinClause extends AbstractModelicaNode {
    ASTWithinClause(int id) {
        super(id);
    }

    ASTWithinClause(ModelicaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ModelicaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public void jjtClose() {
        super.jjtClose();

        ASTName name = getFirstChildOfType(ASTName.class);
        if (name != null) {
            setImage(name.getImage());
        } else {
            setImage("");
        }
    }
}
