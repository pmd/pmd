/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.CatchClause;

public class ASTCatchClause extends AbstractEcmascriptNode<CatchClause> {
    public ASTCatchClause(CatchClause catchClause) {
        super(catchClause);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public ASTName getVariableName() {
        return (ASTName) getChild(0);
    }

    public boolean isIf() {
        return node.getCatchCondition() != null;
    }

    public EcmascriptNode<?> getCatchCondition() {
        return (EcmascriptNode<?>) getChild(1);
    }

    public ASTBlock getBlock() {
        return (ASTBlock) getChild(getNumChildren() - 1);
    }
}
