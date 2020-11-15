/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;

public final class ASTInput extends AbstractPLSQLNode implements RootNode {

    private AstInfo<ASTInput> astInfo;

    ASTInput(int id) {
        super(id);
    }

    @Override
    public AstInfo<ASTInput> getAstInfo() {
        return astInfo;
    }

    ASTInput addTaskInfo(ParserTask task) {
        this.astInfo = new AstInfo<>(task, this);
        return this;
    }


    @Override
    protected <P, R> R acceptPlsqlVisitor(PlsqlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
