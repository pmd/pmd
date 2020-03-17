/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTPrimaryPrefix extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {
    @Deprecated
    @InternalApi
    public ASTPrimaryPrefix(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTPrimaryPrefix(PLSQLParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    private boolean usesSelfModifier;

    @Deprecated
    @InternalApi
    public void setUsesSelfModifier() {
        usesSelfModifier = true;
    }

    public boolean usesSelfModifier() {
        return this.usesSelfModifier;
    }

}
