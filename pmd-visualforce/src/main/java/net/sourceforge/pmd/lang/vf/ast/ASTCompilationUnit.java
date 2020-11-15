/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;

public final class ASTCompilationUnit extends AbstractVfNode implements RootNode {
    private AstInfo<ASTCompilationUnit> astInfo;


    ASTCompilationUnit(int id) {
        super(id);
    }

    @Override
    public AstInfo<ASTCompilationUnit> getAstInfo() {
        return astInfo;
    }

    ASTCompilationUnit makeTaskInfo(ParserTask task) {
        this.astInfo = new AstInfo<>(task, this);
        return this;
    }

    @Override
    protected <P, R> R acceptVfVisitor(VfVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
