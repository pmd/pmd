/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.LetNode;

public final class ASTLetNode extends AbstractEcmascriptNode<LetNode> {
    ASTLetNode(LetNode letNode) {
        super(letNode);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public ASTVariableDeclaration getVariables() {
        return (ASTVariableDeclaration) getChild(0);
    }

    public boolean hasBody() {
        return node.getBody() != null;
    }

    public EcmascriptNode<?> getBody() {
        if (hasBody()) {
            return (EcmascriptNode<?>) getChild(getNumChildren() - 1);
        } else {
            return null;
        }
    }
}
