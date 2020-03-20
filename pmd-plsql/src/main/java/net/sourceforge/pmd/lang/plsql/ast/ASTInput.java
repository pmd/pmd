/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.ast.RootNode;

public final class ASTInput extends AbstractPLSQLNode implements RootNode {

    ASTInput(int id) {
        super(id);
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getSourcecode() {
        return new StringBuilder(getText()).toString();
    }
}
