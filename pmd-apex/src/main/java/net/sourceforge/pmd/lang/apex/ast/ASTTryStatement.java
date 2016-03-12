/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.TryStatement;

public class ASTTryStatement extends AbstractApexNode<TryStatement> {
    public ASTTryStatement(TryStatement tryStatement) {
        super(tryStatement);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public ApexNode<?> getTryBlock() {
        return (ApexNode<?>) jjtGetChild(0);
    }

    @Deprecated // use hasCatch() instead
    public boolean isCatch() {
        return hasCatch();
    }

    public boolean hasCatch() {
	return getNumCatchClause() != 0;
    }

    public int getNumCatchClause() {
        return node.getCatchClauses().size();
    }

    public ASTCatchClause getCatchClause(int index) {
        if (index >= getNumCatchClause()) {
            return null;
        }
        return (ASTCatchClause) jjtGetChild(index + 1);
    }

    @Deprecated // use hasFinally() instead
    public boolean isFinally() {
        return hasFinally();
    }

    public boolean hasFinally() {
        return node.getFinallyBlock() != null;
    }

    public ApexNode<?> getFinallyBlock() {
        if (!hasFinally()) {
            return null;
        }
        return (ApexNode<?>) jjtGetChild(jjtGetNumChildren() - 1);
    }
}
