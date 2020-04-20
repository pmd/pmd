/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.WithStatement;

public final class ASTWithStatement extends AbstractEcmascriptNode<WithStatement> {
    ASTWithStatement(WithStatement withStatement) {
        super(withStatement);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getExpression() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public EcmascriptNode<?> getStatement() {
        return (EcmascriptNode<?>) getChild(1);
    }
}
