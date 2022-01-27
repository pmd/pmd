/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.AstRoot;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.RootNode;

public final class ASTAstRoot extends AbstractEcmascriptNode<AstRoot> implements RootNode {

    private AstInfo<ASTAstRoot> astInfo;

    public ASTAstRoot(AstRoot astRoot) {
        super(astRoot);
    }

    @Override
    public AstInfo<ASTAstRoot> getAstInfo() {
        return astInfo;
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public int getNumComments() {
        return node.getComments() != null ? node.getComments().size() : 0;
    }


    public ASTComment getComment(int index) {
        return (ASTComment) getChild(getNumChildren() - 1 - getNumComments() + index);
    }

    void setAstInfo(AstInfo<ASTAstRoot> astInfo) {
        this.astInfo = astInfo;
    }
}
