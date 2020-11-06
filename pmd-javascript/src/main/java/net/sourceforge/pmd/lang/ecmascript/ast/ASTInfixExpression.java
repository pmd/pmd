/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.InfixExpression;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTInfixExpression extends AbstractInfixEcmascriptNode<InfixExpression> {
    @Deprecated
    @InternalApi
    public ASTInfixExpression(InfixExpression infixExpression) {
        super(infixExpression);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
