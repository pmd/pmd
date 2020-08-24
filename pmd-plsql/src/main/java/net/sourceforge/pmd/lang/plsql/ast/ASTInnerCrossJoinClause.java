/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTInnerCrossJoinClause extends AbstractPLSQLNode {
    private boolean cross;
    private boolean natural;

    ASTInnerCrossJoinClause(int id) {
        super(id);
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
    protected <P, R> R acceptPlsqlVisitor(PLSQLVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
