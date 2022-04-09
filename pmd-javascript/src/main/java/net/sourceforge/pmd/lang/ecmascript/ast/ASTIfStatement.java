/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.IfStatement;

public final class ASTIfStatement extends AbstractEcmascriptNode<IfStatement> {
    ASTIfStatement(IfStatement ifStatement) {
        super(ifStatement);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public boolean hasElse() {
        return node.getElsePart() != null;
    }

    public EcmascriptNode<?> getCondition() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public EcmascriptNode<?> getThen() {
        return (EcmascriptNode<?>) getChild(1);
    }

    public EcmascriptNode<?> getElse() {
        return (EcmascriptNode<?>) getChild(2);
    }
}
