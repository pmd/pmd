/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTOuterJoinClause extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
    private boolean natural;

    @Deprecated
    @InternalApi
    public ASTOuterJoinClause(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTOuterJoinClause(PLSQLParser p, int id) {
        super(p, id);
    }

    public boolean isNatural() {
        return natural;
    }

    void setNatural(boolean natural) {
        this.natural = natural;
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
