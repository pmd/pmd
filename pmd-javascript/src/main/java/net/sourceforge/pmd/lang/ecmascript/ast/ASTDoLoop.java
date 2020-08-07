/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.DoLoop;

public final class ASTDoLoop extends AbstractEcmascriptNode<DoLoop> {
    ASTDoLoop(DoLoop doLoop) {
        super(doLoop);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getBody() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public EcmascriptNode<?> getCondition() {
        return (EcmascriptNode<?>) getChild(1);
    }
}
