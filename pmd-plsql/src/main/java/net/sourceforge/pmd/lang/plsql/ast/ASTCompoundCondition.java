/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.Locale;

public final class ASTCompoundCondition extends AbstractPLSQLNode {
    private String type;

    ASTCompoundCondition(int id) {
        super(id);
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
