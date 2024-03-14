/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity.ast;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;

public final class ASTTemplate extends AbstractVtlNode implements RootNode {

    private AstInfo<ASTTemplate> astInfo;

    public ASTTemplate(int id) {
        super(id);
    }

    @Override
    public AstInfo<ASTTemplate> getAstInfo() {
        return astInfo;
    }

    ASTTemplate makeTaskInfo(ParserTask task) {
        this.astInfo = new AstInfo<>(task, this);
        return this;
    }


    @Override
    protected <P, R> R acceptVtlVisitor(VtlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
