/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public class ASTRegexpLikeCondition extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
    private String matchParam;

    public ASTRegexpLikeCondition(int id) {
        super(id);
    }

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
/* JavaCC - OriginalChecksum=afb8806a0c67f95b736d6e8bc46def15 (do not edit this line) */
