/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTArguments extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
    @Deprecated
    @InternalApi
    public ASTArguments(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTArguments(PLSQLParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public int getArgumentCount() {
        if (this.getNumChildren() == 0) {
            return 0;
        }
        return this.getChild(0).getNumChildren();
    }
}
