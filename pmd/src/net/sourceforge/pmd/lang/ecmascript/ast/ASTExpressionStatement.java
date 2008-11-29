/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.ExpressionStatement;

public class ASTExpressionStatement extends AbstractEcmascriptNode<ExpressionStatement> {
    public ASTExpressionStatement(ExpressionStatement expressionStatement) {
	super(expressionStatement);
    }

    public boolean hasResult() {
	return node.getType() == Token.EXPR_RESULT;
    }
}
