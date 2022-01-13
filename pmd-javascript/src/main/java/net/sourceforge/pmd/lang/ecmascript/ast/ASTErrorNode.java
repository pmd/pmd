/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ErrorNode;

public final class ASTErrorNode extends AbstractEcmascriptNode<ErrorNode> {

    ASTErrorNode(ErrorNode errorNode) {
        super(errorNode);
        super.setImage(errorNode.getMessage());
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getMessage() {
        return node.getMessage();
    }
}
