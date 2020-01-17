/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ConditionalExpression;

public class ASTConditionalExpression extends AbstractEcmascriptNode<ConditionalExpression> {
    public ASTConditionalExpression(ConditionalExpression conditionalExpression) {
        super(conditionalExpression);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getTestExpression() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public EcmascriptNode<?> getTrueExpression() {
        return (EcmascriptNode<?>) getChild(1);
    }

    public EcmascriptNode<?> getFalseExpression() {
        return (EcmascriptNode<?>) getChild(2);
    }
}
