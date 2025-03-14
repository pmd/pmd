/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ReturnStatement;

public final class ASTReturnStatement extends AbstractEcmascriptNode<ReturnStatement> {
    ASTReturnStatement(ReturnStatement returnStatement) {
        super(returnStatement);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public boolean hasResult() {
        return node.getReturnValue() != null;
    }
}
