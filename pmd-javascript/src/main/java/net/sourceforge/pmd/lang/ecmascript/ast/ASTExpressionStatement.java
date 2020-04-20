/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.ExpressionStatement;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTExpressionStatement extends AbstractEcmascriptNode<ExpressionStatement> {
    @Deprecated
    @InternalApi
    public ASTExpressionStatement(ExpressionStatement expressionStatement) {
        super(expressionStatement);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean hasResult() {
        return node.getType() == Token.EXPR_RESULT;
    }
}
