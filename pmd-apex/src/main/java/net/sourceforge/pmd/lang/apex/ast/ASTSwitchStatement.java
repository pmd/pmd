/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.SwitchStatement;

public class ASTSwitchStatement extends AbstractApexNode<SwitchStatement> {
    public ASTSwitchStatement(SwitchStatement switchStatement) {
	super(switchStatement);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ApexNode<?> getExpression() {
	return (ApexNode<?>) jjtGetChild(0);
    }

    public int getNumCases() {
	return node.getCases().size();
    }

    public ASTSwitchCase getSwitchCase(int index) {
	return (ASTSwitchCase) jjtGetChild(index + 1);
    }
}
