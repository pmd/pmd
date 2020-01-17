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
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getTryBlock() {
        return (EcmascriptNode<?>) getChild(0);
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
        return (ASTCatchClause) getChild(index + 1);
    }

    @Deprecated // use hasFinally() instead
    public boolean isFinally() {
        return hasFinally();
    }

    public boolean hasFinally() {
        return node.getFinallyBlock() != null;
    }

    public EcmascriptNode<?> getFinallyBlock() {
        if (!hasFinally()) {
            return null;
        }
        return (EcmascriptNode<?>) getChild(getNumChildren() - 1);
    }
}
