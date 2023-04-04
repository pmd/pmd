/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.CatchClause;

public final class ASTCatchClause extends AbstractEcmascriptNode<CatchClause> {
    ASTCatchClause(CatchClause catchClause) {
        super(catchClause);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
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
