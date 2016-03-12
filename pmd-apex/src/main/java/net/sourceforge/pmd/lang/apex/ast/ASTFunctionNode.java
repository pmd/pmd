/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.FunctionNode;

public class ASTFunctionNode extends AbstractApexNode<FunctionNode> {
    public ASTFunctionNode(FunctionNode functionNode) {
	super(functionNode);
	super.setImage(functionNode.getName());
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public int getNumParams() {
	return node.getParams().size();
    }

    public ASTName getFunctionName() {
	if (node.getFunctionName() != null) {
	    return (ASTName) jjtGetChild(0);
	}
	return null;
    }

    public ApexNode<?> getParam(int index) {
        int paramIndex = index;
	if (node.getFunctionName() != null) {
	    paramIndex = index + 1;
	}
	return (ApexNode<?>) jjtGetChild(paramIndex);
    }

    public ApexNode<?> getBody() {
        return (ApexNode<?>) jjtGetChild(jjtGetNumChildren() - 1);
    }

    @Deprecated // use getBody() instead
    public ApexNode<?> getBody(int index) {
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
