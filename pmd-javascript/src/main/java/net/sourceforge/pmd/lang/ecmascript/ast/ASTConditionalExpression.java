/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ConditionalExpression;

public final class ASTConditionalExpression extends AbstractEcmascriptNode<ConditionalExpression> {
    ASTConditionalExpression(ConditionalExpression conditionalExpression) {
        super(conditionalExpression);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getTestExpression() {
        return getChild(0);
    }

    public EcmascriptNode<?> getTrueExpression() {
        return getChild(1);
    }

    public EcmascriptNode<?> getFalseExpression() {
        return getChild(2);
    }
}
