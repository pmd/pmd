/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTFormalParameter extends AbstractPLSQLNode {

    ASTFormalParameter(int id) {
        super(id);
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public ASTDatatype getTypeNode() {
        for (int i = 0; i < getNumChildren(); i++) {
            if (getChild(i) instanceof ASTDatatype) {
                return (ASTDatatype) getChild(i);
            }
        }
        throw new IllegalStateException("ASTType not found");
    }
}
