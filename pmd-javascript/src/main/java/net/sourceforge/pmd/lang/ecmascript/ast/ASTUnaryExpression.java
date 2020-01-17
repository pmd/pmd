/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.UnaryExpression;

public class ASTUnaryExpression extends AbstractEcmascriptNode<UnaryExpression> {
    public ASTUnaryExpression(UnaryExpression unaryExpression) {
        super(unaryExpression);
        if (unaryExpression.getOperator() == Token.VOID) {
            super.setImage("void");
        } else {
            super.setImage(AstRoot.operatorToString(unaryExpression.getOperator()));
        }
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getOperand() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public boolean isPrefix() {
        return node.isPrefix();
    }

    public boolean isPostfix() {
        return node.isPostfix();
    }
}
