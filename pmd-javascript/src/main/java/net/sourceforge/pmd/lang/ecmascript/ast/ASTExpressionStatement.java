/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.ExpressionStatement;

public final class ASTExpressionStatement extends AbstractEcmascriptNode<ExpressionStatement> {
    ASTExpressionStatement(ExpressionStatement expressionStatement) {
        super(expressionStatement);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public boolean hasResult() {
        return node.getType() == Token.EXPR_RESULT;
    }
}
