/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.InfixExpression;

public class AbstractInfixEcmascriptNode<T extends InfixExpression> extends AbstractEcmascriptNode<T> {

    public AbstractInfixEcmascriptNode(T infixExpression) {
        this(infixExpression, true);
    }

    public AbstractInfixEcmascriptNode(T infixExpression, boolean setImage) {
        super(infixExpression);
        if (setImage) {
            if (infixExpression.getOperator() == Token.ASSIGN_BITXOR) {
                super.setImage("^=");
            } else {
                super.setImage(AstRoot.operatorToString(infixExpression.getOperator()));
            }
        }
    }

    public EcmascriptNode<?> getLeft() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public EcmascriptNode<?> getRight() {
        return (EcmascriptNode<?>) getChild(1);
    }
}
