/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ContinueStatement;

public class ASTContinueStatement extends AbstractEcmascriptNode<ContinueStatement> {
    public ASTContinueStatement(ContinueStatement continueStatement) {
        super(continueStatement);
        super.setImage(continueStatement.getLabel() != null ? continueStatement.getLabel().getIdentifier() : null);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean hasLabel() {
        return node.getLabel() != null;
    }

    public ASTName getLabel() {
        return (ASTName) getChild(0);
    }
}
