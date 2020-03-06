/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.RootNode;

public class ASTInput extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode implements RootNode {
    private String sourcecode;

    @Deprecated
    @InternalApi
    public ASTInput(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTInput(PLSQLParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    void setSourcecode(String sourcecode) {
        this.sourcecode = sourcecode;
    }

    public String getSourcecode() {
        return sourcecode;
    }
}
