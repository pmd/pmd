/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.DoLoop;

public class ASTDoLoop extends AbstractEcmascriptNode<DoLoop> {
    public ASTDoLoop(DoLoop doLoop) {
        super(doLoop);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getBody() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public EcmascriptNode<?> getCondition() {
        return (EcmascriptNode<?>) getChild(1);
    }
}
