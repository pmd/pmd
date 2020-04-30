/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTFetchStatement extends AbstractPLSQLNode {
    private boolean bulkcollect;
    private boolean limit;

    @Deprecated
    @InternalApi
    public ASTFetchStatement(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTFetchStatement(PLSQLParser p, int id) {
        super(p, id);
    }

    void setBulkCollect(boolean bulkcollect) {
        this.bulkcollect = bulkcollect;
    }

    public boolean isBulkCollect() {
        return this.bulkcollect;
    }

    void setLimit(boolean limit) {
        this.limit = limit;
    }

    public boolean isLimit() {
        return this.limit;
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
