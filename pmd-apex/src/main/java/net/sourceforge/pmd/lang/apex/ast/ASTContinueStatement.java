/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.statement.ContinueStatement;

public final class ASTContinueStatement extends AbstractApexNode.Single<ContinueStatement> {

    ASTContinueStatement(ContinueStatement continueStatement) {
        super(continueStatement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
