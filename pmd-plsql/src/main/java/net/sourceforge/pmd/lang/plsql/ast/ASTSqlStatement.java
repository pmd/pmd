/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTSqlStatement extends AbstractPLSQLNode {

    private Type type;

    public enum Type { COMMIT, ROLLBACK, SAVEPOINT, SET_TRANSACTION, LOCK_TABLE, MERGE }

    ASTSqlStatement(int id) {
        super(id);
    }

    void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
