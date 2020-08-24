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
    protected <P, R> R acceptPlsqlVisitor(PLSQLVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getSourcecode() {
        return new StringBuilder(getText()).toString();
    }
}
