/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

public class ASTElementList extends AbstractModelicaNode {
    private Visibility visibility;

    ASTElementList(int id) {
        super(id);
    }

    ASTElementList(ModelicaParser p, int id) {
        super(p, id);
    }

    void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    @Override
    public Object jjtAccept(ModelicaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
