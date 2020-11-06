/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTRegexpLikeCondition extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
    private String matchParam;

    @Deprecated
    @InternalApi
    public ASTRegexpLikeCondition(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTRegexpLikeCondition(PLSQLParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    void setMatchParam(String matchParam) {
        this.matchParam = matchParam;
    }

    public String getMatchParam() {
        return this.matchParam;
    }

    public ASTSqlExpression getSourceChar() {
        return (ASTSqlExpression) getChild(0);
    }

    public ASTSqlExpression getPattern() {
        return (ASTSqlExpression) getChild(1);
    }
}
