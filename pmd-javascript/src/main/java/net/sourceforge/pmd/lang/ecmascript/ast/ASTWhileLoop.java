/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.WhileLoop;

public class ASTWhileLoop extends AbstractEcmascriptNode<WhileLoop> {
    public ASTWhileLoop(WhileLoop whileLoop) {
        super(whileLoop);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getCondition() {
        return (EcmascriptNode<?>) jjtGetChild(0);
    }

    public EcmascriptNode<?> getBody() {
        return (EcmascriptNode<?>) jjtGetChild(1);
    }
}
