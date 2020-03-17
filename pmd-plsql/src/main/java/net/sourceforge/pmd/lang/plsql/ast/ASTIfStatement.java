/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTIfStatement extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
    @Deprecated
    @InternalApi
    public ASTIfStatement(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTIfStatement(PLSQLParser p, int id) {
        super(p, id);
    }

    private boolean hasElse;

    @Deprecated
    @InternalApi
    public void setHasElse() {
        this.hasElse = true;
    }

    public boolean hasElse() {
        return this.hasElse;
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
