/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public class ASTSimpleExpression extends AbstractPLSQLNode {
    private boolean joinOperator;

    ASTSimpleExpression(int id) {
        super(id);
    }

    ASTSimpleExpression(PLSQLParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    void setJoinOperator(boolean joinOperator) {
        this.joinOperator = joinOperator;
    }

    public boolean hasJoinOperator() {
        return joinOperator;
    }
}
