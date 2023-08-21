/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.FunctionNode;

public final class ASTFunctionNode extends AbstractEcmascriptNode<FunctionNode> {
    ASTFunctionNode(FunctionNode functionNode) {
        super(functionNode);
        super.setImage(functionNode.getName());
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public int getNumParams() {
        return node.getParams().size();
    }

    public ASTName getFunctionName() {
        if (node.getFunctionName() != null) {
            return (ASTName) getChild(0);
        }
        return null;
    }

    public EcmascriptNode<?> getParam(int index) {
        int paramIndex = index;
        if (node.getFunctionName() != null) {
            paramIndex = index + 1;
        }
        return (EcmascriptNode<?>) getChild(paramIndex);
    }

    public EcmascriptNode<?> getBody() {
        return (EcmascriptNode<?>) getChild(getNumChildren() - 1);
    }

    @Deprecated // use getBody() instead
    public EcmascriptNode<?> getBody(int index) {
        return getBody();
    }

    public boolean isClosure() {
        return node.isExpressionClosure();
    }

    public boolean isGetter() {
        return node.isGetterMethod();
    }

    public boolean isSetter() {
        return node.isSetterMethod();
    }

    public boolean isGetterOrSetter() {
        return isGetter() || isSetter();
    }
}
