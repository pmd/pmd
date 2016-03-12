/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.WhileLoop;

public class ASTWhileLoop extends AbstractApexNode<WhileLoop> {
    public ASTWhileLoop(WhileLoop whileLoop) {
	super(whileLoop);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ApexNode<?> getCondition() {
	return (ApexNode<?>) jjtGetChild(0);
    }

    public ApexNode<?> getBody() {
	return (ApexNode<?>) jjtGetChild(1);
    }
}
