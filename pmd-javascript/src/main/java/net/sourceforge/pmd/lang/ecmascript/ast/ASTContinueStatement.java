/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ContinueStatement;

public final class ASTContinueStatement extends AbstractEcmascriptNode<ContinueStatement> {
    ASTContinueStatement(ContinueStatement continueStatement) {
        super(continueStatement);
        super.setImage(continueStatement.getLabel() != null ? continueStatement.getLabel().getIdentifier() : null);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public boolean hasLabel() {
        return node.getLabel() != null;
    }

    public ASTName getLabel() {
        return (ASTName) getChild(0);
    }
}
