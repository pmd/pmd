/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.NewExpression;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTNewExpression extends AbstractFunctionCallNode<NewExpression> {
    @Deprecated
    @InternalApi
    public ASTNewExpression(NewExpression newExpression) {
        super(newExpression);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean hasInitializer() {
        return node.getInitializer() != null;
    }

    public ASTObjectLiteral getInitializer() {
        return (ASTObjectLiteral) getChild(getNumChildren() - 1);
    }
}
