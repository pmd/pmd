/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTFormalParameter extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
    @Deprecated
    @InternalApi
    public ASTFormalParameter(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTFormalParameter(PLSQLParser p, int id) {
        super(p, id);
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
