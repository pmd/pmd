/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.TryStatement;

public class ASTTryStatement extends AbstractEcmascriptNode<TryStatement> {
    public ASTTryStatement(TryStatement tryStatement) {
	super(tryStatement);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public EcmascriptNode getTryBlock() {
	return (EcmascriptNode) jjtGetChild(0);
    }

    public boolean isCatch() {
	return getNumCatchClause() != 0;
    }

    public int getNumCatchClause() {
	return node.getCatchClauses().size();
    }

    public ASTCatchClause getCatchClause(int index) {
	return (ASTCatchClause) jjtGetChild(index - 1);
    }

    public boolean isFinally() {
	return node.getFinallyBlock() != null;
    }

    public EcmascriptNode getFinallyBlock() {
	return (EcmascriptNode) jjtGetChild(jjtGetNumChildren() - 1);
    }
}
