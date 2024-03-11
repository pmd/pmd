/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;

import com.google.summit.ast.statement.TryStatement;

public final class ASTTryCatchFinallyBlockStatement extends AbstractApexNode.Single<TryStatement> {

    ASTTryCatchFinallyBlockStatement(TryStatement tryStatement) {
        super(tryStatement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public ASTBlockStatement getTryBlock() {
        return (ASTBlockStatement) getChild(0);
    }

    public List<ASTCatchBlockStatement> getCatchClauses() {
        return children(ASTCatchBlockStatement.class).toList();
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
