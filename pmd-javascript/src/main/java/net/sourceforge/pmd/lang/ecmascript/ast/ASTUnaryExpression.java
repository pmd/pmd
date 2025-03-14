/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.UnaryExpression;

public final class ASTUnaryExpression extends AbstractEcmascriptNode<UnaryExpression> {
    ASTUnaryExpression(UnaryExpression unaryExpression) {
        super(unaryExpression);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getOperand() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public boolean isPrefix() {
        return !isPostfix();
    }

    public boolean isPostfix() {
        return node.getOperator() == Token.INC || node.getOperator() == Token.DEC;
    }

    public String getOperator() {
        return AstRoot.operatorToString(node.getOperator());
    }
}
