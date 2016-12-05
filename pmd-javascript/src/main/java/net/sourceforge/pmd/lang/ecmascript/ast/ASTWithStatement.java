/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.WithStatement;

public class ASTWithStatement extends AbstractEcmascriptNode<WithStatement> {
    public ASTWithStatement(WithStatement withStatement) {
        super(withStatement);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getExpression() {
        return (EcmascriptNode<?>) jjtGetChild(0);
    }

    public EcmascriptNode<?> getStatement() {
        return (EcmascriptNode<?>) jjtGetChild(1);
    }
}
