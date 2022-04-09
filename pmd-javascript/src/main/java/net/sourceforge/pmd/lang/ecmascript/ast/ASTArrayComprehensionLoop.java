/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ArrayComprehensionLoop;

public final class ASTArrayComprehensionLoop extends AbstractEcmascriptNode<ArrayComprehensionLoop> {
    ASTArrayComprehensionLoop(ArrayComprehensionLoop arrayComprehensionLoop) {
        super(arrayComprehensionLoop);
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
}
