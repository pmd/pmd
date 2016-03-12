/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.BreakStatement;

public class ASTBreakStatement extends AbstractApexNode<BreakStatement> {
    public ASTBreakStatement(BreakStatement breakStatement) {
	super(breakStatement);
	super.setImage(breakStatement.getBreakLabel() != null ? breakStatement.getBreakLabel().getIdentifier() : null);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean hasLabel() {
	return node.getBreakLabel() != null;
    }

    public ASTName getLabel() {
	return (ASTName) jjtGetChild(0);
    }
}
