/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ErrorNode;

public final class ASTErrorNode extends AbstractEcmascriptNode<ErrorNode> {

    ASTErrorNode(ErrorNode errorNode) {
        super(errorNode);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getMessage() {
        return node.getMessage();
    }
}
