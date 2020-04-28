/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import java.util.Collections;
import java.util.Map;

import org.mozilla.javascript.ast.AstRoot;

import net.sourceforge.pmd.lang.ast.RootNode;

public final class ASTAstRoot extends AbstractEcmascriptNode<AstRoot> implements RootNode {

    private Map<Integer, String> noPmdComments = Collections.emptyMap();

    public ASTAstRoot(AstRoot astRoot) {
        super(astRoot);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public int getNumComments() {
        return node.getComments() != null ? node.getComments().size() : 0;
    }

    @Override
    public Map<Integer, String> getNoPmdComments() {
        return noPmdComments;
    }

    void setNoPmdComments(Map<Integer, String> noPmdComments) {
        this.noPmdComments = noPmdComments;
    }

    public ASTComment getComment(int index) {
        return (ASTComment) getChild(getNumChildren() - 1 - getNumComments() + index);
    }
}
