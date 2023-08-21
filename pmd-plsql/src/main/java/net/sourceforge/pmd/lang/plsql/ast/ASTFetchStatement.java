/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTFetchStatement extends AbstractPLSQLNode {
    private boolean bulkcollect;
    private boolean limit;


    ASTFetchStatement(int id) {
        super(id);
    }

    void setBulkCollect(boolean bulkcollect) {
        this.bulkcollect = bulkcollect;
    }

    public boolean isBulkCollect() {
        return this.bulkcollect;
    }

    void setLimit(boolean limit) {
        this.limit = limit;
    }

    public boolean isLimit() {
        return this.limit;
    }

    @Override
    protected <P, R> R acceptPlsqlVisitor(PlsqlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
