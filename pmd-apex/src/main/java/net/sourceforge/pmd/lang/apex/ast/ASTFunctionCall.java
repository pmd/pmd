/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.FunctionCall;

public class ASTFunctionCall extends AbstractApexNode<FunctionCall> {
    public ASTFunctionCall(FunctionCall functionCall) {
	super(functionCall);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ApexNode<?> getTarget() {
	return (ApexNode<?>) jjtGetChild(0);
    }

    public int getNumArguments() {
	return node.getArguments().size();
    }

    public ApexNode<?> getArgument(int index) {
	return (ApexNode<?>) jjtGetChild(index + 1);
    }

    public boolean hasArguments() {
	return getNumArguments() != 0;
    }
}
