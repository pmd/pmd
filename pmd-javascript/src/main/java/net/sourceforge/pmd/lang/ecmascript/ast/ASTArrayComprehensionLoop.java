/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ArrayComprehensionLoop;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTArrayComprehensionLoop extends AbstractEcmascriptNode<ArrayComprehensionLoop> {
    @Deprecated
    @InternalApi
    public ASTArrayComprehensionLoop(ArrayComprehensionLoop arrayComprehensionLoop) {
        super(arrayComprehensionLoop);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getIterator() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public EcmascriptNode<?> getIteratedObject() {
        return (EcmascriptNode<?>) getChild(1);
    }
}
