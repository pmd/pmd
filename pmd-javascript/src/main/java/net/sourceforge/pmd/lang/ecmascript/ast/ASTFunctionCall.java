/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.FunctionCall;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTFunctionCall extends AbstractEcmascriptNode<FunctionCall> {
    @Deprecated
    @InternalApi
    public ASTFunctionCall(FunctionCall functionCall) {
        super(functionCall);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
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
}
