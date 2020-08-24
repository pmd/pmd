/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTArguments extends AbstractPLSQLNode {

    ASTArguments(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptPlsqlVisitor(PLSQLVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public int getArgumentCount() {
        if (this.getNumChildren() == 0) {
            return 0;
        }
        return this.getChild(0).getNumChildren();
    }
}
