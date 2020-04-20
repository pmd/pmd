/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.WithStatement;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTWithStatement extends AbstractEcmascriptNode<WithStatement> {
    @Deprecated
    @InternalApi
    public ASTWithStatement(WithStatement withStatement) {
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
