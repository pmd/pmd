/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.NewExpression;

public final class ASTNewExpression extends AbstractEcmascriptNode<NewExpression> {
    ASTNewExpression(NewExpression newExpression) {
        super(newExpression);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getTarget() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public int getNumArguments() {
        return node.getArguments().size();
    }

    public EcmascriptNode<?> getArgument(int index) {
        return (EcmascriptNode<?>) getChild(index + 1);
    }

    public boolean hasArguments() {
        return getNumArguments() != 0;
    }

    public boolean hasInitializer() {
        return node.getInitializer() != null;
    }

    public ASTObjectLiteral getInitializer() {
        return (ASTObjectLiteral) getChild(getNumChildren() - 1);
    }
}
