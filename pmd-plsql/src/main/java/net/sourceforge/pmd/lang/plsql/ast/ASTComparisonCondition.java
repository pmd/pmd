/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTComparisonCondition extends AbstractPLSQLNode {
    private String operator;

    @Deprecated
    @InternalApi
    public ASTComparisonCondition(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTComparisonCondition(PLSQLParser p, int id) {
        super(p, id);
    }

    void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return this.operator;
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
