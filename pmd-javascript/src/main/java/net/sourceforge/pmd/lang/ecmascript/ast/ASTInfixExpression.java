/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.InfixExpression;

public final class ASTInfixExpression extends AbstractInfixEcmascriptNode<InfixExpression> {
    ASTInfixExpression(InfixExpression infixExpression) {
        super(infixExpression);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
