/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTIfStatement extends AbstractPLSQLNode {
    private boolean hasElse;

    ASTIfStatement(int id) {
        super(id);
    }

    void setHasElse() {
        this.hasElse = true;
    }

    public boolean hasElse() {
        return this.hasElse;
    }

    @Override
    protected <P, R> R acceptPlsqlVisitor(PLSQLVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
