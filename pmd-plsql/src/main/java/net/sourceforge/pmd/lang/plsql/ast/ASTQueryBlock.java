/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTQueryBlock extends AbstractSelectStatement {
    @Deprecated
    @InternalApi
    public ASTQueryBlock(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTQueryBlock(PLSQLParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
