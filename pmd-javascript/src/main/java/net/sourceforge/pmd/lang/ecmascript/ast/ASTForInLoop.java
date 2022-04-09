/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ForInLoop;

public final class ASTForInLoop extends AbstractEcmascriptNode<ForInLoop> {
    ASTForInLoop(ForInLoop forInLoop) {
        super(forInLoop);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getIterator() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public EcmascriptNode<?> getIteratedObject() {
        return (EcmascriptNode<?>) getChild(1);
    }

    public EcmascriptNode<?> getBody() {
        return (EcmascriptNode<?>) getChild(2);
    }

    public boolean isForEach() {
        return node.isForEach();
    }

    public boolean isForOf() {
        return node.isForOf();
    }
}
