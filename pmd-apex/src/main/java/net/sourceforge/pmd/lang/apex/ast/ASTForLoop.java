/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.ForLoop;

public class ASTForLoop extends AbstractApexNode<ForLoop> {
    public ASTForLoop(ForLoop forLoop) {
	super(forLoop);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ApexNode<?> getInitializer() {
	return (ApexNode<?>) jjtGetChild(0);
    }

    public ApexNode<?> getCondition() {
	return (ApexNode<?>) jjtGetChild(1);
    }

    public ApexNode<?> getIncrement() {
	return (ApexNode<?>) jjtGetChild(2);
    }

    public ApexNode<?> getBody() {
	return (ApexNode<?>) jjtGetChild(3);
    }
}
