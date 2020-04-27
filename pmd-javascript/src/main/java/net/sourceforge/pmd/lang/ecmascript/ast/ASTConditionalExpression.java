/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ConditionalExpression;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTConditionalExpression extends AbstractEcmascriptNode<ConditionalExpression> {
    @Deprecated
    @InternalApi
    public ASTConditionalExpression(ConditionalExpression conditionalExpression) {
        super(conditionalExpression);
    }

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
