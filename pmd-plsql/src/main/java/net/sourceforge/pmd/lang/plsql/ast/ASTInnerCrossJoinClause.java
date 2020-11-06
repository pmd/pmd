/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTInnerCrossJoinClause extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
    private boolean cross;
    private boolean natural;

    @Deprecated
    @InternalApi
    public ASTInnerCrossJoinClause(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTInnerCrossJoinClause(PLSQLParser p, int id) {
        super(p, id);
    }

    public boolean isCross() {
        return cross;
    }

    public boolean isNatural() {
        return natural;
    }

    void setCross(boolean cross) {
        this.cross = cross;
    }

    void setNatural(boolean natural) {
        this.natural = natural;
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
