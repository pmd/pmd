/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;

import apex.jorje.semantic.ast.statement.TryCatchFinallyBlockStatement;

public final class ASTTryCatchFinallyBlockStatement extends AbstractApexNode<TryCatchFinallyBlockStatement> {

    ASTTryCatchFinallyBlockStatement(TryCatchFinallyBlockStatement tryCatchFinallyBlockStatement) {
        super(tryCatchFinallyBlockStatement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public ASTBlockStatement getTryBlock() {
        return (ASTBlockStatement) getChild(0);
    }

    public List<ASTCatchBlockStatement> getCatchClauses() {
        return findChildrenOfType(ASTCatchBlockStatement.class);
    }

    public ASTBlockStatement getFinallyBlock() {
        ApexNode<?> lastChild = null;
        if (getNumChildren() >= 2) {
            lastChild = getChild(getNumChildren() - 1);
        }
        if (lastChild instanceof ASTBlockStatement) {
            return (ASTBlockStatement) lastChild;
        }
        return null;
    }
}
