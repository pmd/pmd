/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTSelectStatement extends AbstractSelectStatement {

    public ASTSelectStatement(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptPlsqlVisitor(PlsqlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public ASTFromClause getFromClause() {
        return getFirstChildOfType(ASTFromClause.class);
    }
}
