/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.ReturnStatement;

public class ASTReturnStatement extends AbstractApexNode<ReturnStatement> {
    public ASTReturnStatement(ReturnStatement returnStatement) {
	super(returnStatement);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean hasResult() {
	return node.getReturnValue() != null;
    }
}
