/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTOuterJoinClause extends AbstractPLSQLNode {
    private boolean natural;

    ASTOuterJoinClause(int id) {
        super(id);
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
