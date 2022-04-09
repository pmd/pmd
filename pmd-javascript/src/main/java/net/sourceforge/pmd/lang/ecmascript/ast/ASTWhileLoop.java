/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.WhileLoop;

public final class ASTWhileLoop extends AbstractEcmascriptNode<WhileLoop> {
    ASTWhileLoop(WhileLoop whileLoop) {
        super(whileLoop);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getCondition() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public EcmascriptNode<?> getBody() {
        return (EcmascriptNode<?>) getChild(1);
    }
}
