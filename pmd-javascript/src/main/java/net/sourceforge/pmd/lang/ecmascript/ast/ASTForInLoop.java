/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ForInLoop;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTForInLoop extends AbstractEcmascriptNode<ForInLoop> {
    @Deprecated
    @InternalApi
    public ASTForInLoop(ForInLoop forInLoop) {
        super(forInLoop);
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
