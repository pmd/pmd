/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.FunctionCall;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTFunctionCall extends AbstractFunctionCallNode<FunctionCall> {
    @Deprecated
    @InternalApi
    public ASTFunctionCall(FunctionCall functionCall) {
        super(functionCall);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
