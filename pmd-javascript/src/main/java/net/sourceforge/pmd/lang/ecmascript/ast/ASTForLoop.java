/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ForLoop;

public final class ASTForLoop extends AbstractEcmascriptNode<ForLoop> {
    ASTForLoop(ForLoop forLoop) {
        super(forLoop);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getInitializer() {
        return getChild(0);
    }

    public EcmascriptNode<?> getCondition() {
        return getChild(1);
    }

    public EcmascriptNode<?> getIncrement() {
        return getChild(2);
    }

    public EcmascriptNode<?> getBody() {
        return getChild(3);
    }
}
