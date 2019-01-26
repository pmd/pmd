/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.Locale;

public class ASTCompoundCondition extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
    private String type;

    public ASTCompoundCondition(int id) {
        super(id);
    }

    public ASTCompoundCondition(PLSQLParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
        if (this.type != null) {
            this.type = this.type.toUpperCase(Locale.ROOT);
        }
    }
}
/* JavaCC - OriginalChecksum=e16db32ec742f0a3836eafa8756cfbc2 (do not edit this line) */
