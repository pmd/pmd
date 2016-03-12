/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.ContinueStatement;

public class ASTContinueStatement extends AbstractApexNode<ContinueStatement> {
    public ASTContinueStatement(ContinueStatement continueStatement) {
	super(continueStatement);
	super.setImage(continueStatement.getLabel() != null ? continueStatement.getLabel().getIdentifier() : null);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean hasLabel() {
	return node.getLabel() != null;
    }

    public ASTName getLabel() {
	return (ASTName) jjtGetChild(0);
    }
}
