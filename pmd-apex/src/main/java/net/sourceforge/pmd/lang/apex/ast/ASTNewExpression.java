/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.NewExpression;

public class ASTNewExpression extends AbstractApexNode<NewExpression> {
    public ASTNewExpression(NewExpression newExpression) {
	super(newExpression);
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

    public boolean hasInitializer() {
	return node.getInitializer() != null;
    }

    public ASTObjectLiteral getInitializer() {
	return (ASTObjectLiteral) jjtGetChild(jjtGetNumChildren() - 1);
    }
}
