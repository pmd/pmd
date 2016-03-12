/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.DoLoop;

public class ASTDoLoop extends AbstractApexNode<DoLoop> {
    public ASTDoLoop(DoLoop doLoop) {
	super(doLoop);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ApexNode<?> getBody() {
	return (ApexNode<?>) jjtGetChild(0);
    }

    public ApexNode<?> getCondition() {
	return (ApexNode<?>) jjtGetChild(1);
    }
}
