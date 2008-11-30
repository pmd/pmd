/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ReturnStatement;

public class ASTReturnStatement extends AbstractEcmascriptNode<ReturnStatement> {
    public ASTReturnStatement(ReturnStatement returnStatement) {
	super(returnStatement);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean hasResult() {
	return node.getReturnValue() != null;
    }
}
