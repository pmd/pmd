/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.ForInLoop;

public class ASTForInLoop extends AbstractApexNode<ForInLoop> {
    public ASTForInLoop(ForInLoop forInLoop) {
	super(forInLoop);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ApexNode<?> getIterator() {
	return (ApexNode<?>) jjtGetChild(0);
    }

    public ApexNode<?> getIteratedObject() {
	return (ApexNode<?>) jjtGetChild(1);
    }

    public ApexNode<?> getBody() {
	return (ApexNode<?>) jjtGetChild(2);
    }

    public boolean isForEach() {
	return node.isForEach();
    }
}
