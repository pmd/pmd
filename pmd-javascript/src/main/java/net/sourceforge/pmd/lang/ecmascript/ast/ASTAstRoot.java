/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.AstRoot;

import net.sourceforge.pmd.lang.ast.RootNode;

public class ASTAstRoot extends AbstractEcmascriptNode<AstRoot> implements RootNode {
    public ASTAstRoot(AstRoot astRoot) {
        super(astRoot);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public int getNumComments() {
        return node.getComments() != null ? node.getComments().size() : 0;
    }

    public ASTComment getComment(int index) {
        return (ASTComment) getChild(getNumChildren() - 1 - getNumComments() + index);
    }
}
