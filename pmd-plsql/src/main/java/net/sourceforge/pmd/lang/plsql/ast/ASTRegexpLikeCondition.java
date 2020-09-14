/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTRegexpLikeCondition extends AbstractPLSQLNode {
    private String matchParam;

    ASTRegexpLikeCondition(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptPlsqlVisitor(PlsqlVisitor<? super P, ? extends R> visitor, P data) {
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
