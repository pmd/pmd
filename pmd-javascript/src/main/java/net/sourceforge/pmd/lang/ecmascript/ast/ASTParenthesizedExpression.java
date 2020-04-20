/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ParenthesizedExpression;

public final class ASTParenthesizedExpression extends AbstractEcmascriptNode<ParenthesizedExpression> {
    ASTParenthesizedExpression(ParenthesizedExpression parenthesizedExpression) {
        super(parenthesizedExpression);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
