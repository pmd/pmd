/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.IfStatement;

public class ASTIfStatement extends AbstractApexNode<IfStatement> {
    public ASTIfStatement(IfStatement ifStatement) {
	super(ifStatement);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean hasElse() {
	return node.getElsePart() != null;
    }

    public ApexNode<?> getCondition() {
	return (ApexNode<?>) jjtGetChild(0);
    }

    public ApexNode<?> getThen() {
	return (ApexNode<?>) jjtGetChild(1);
    }

    public ApexNode<?> getElse() {
	return (ApexNode<?>) jjtGetChild(2);
    }
}
