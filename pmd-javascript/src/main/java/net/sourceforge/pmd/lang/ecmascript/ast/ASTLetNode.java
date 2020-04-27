/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.LetNode;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTLetNode extends AbstractEcmascriptNode<LetNode> {
    @Deprecated
    @InternalApi
    public ASTLetNode(LetNode letNode) {
        super(letNode);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
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
