/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.statement.IfStatement;

public final class ASTIfElseBlockStatement extends AbstractApexNode.Single<IfStatement> {

    private final boolean hasElseStatement;

    ASTIfElseBlockStatement(IfStatement ifStatement, boolean hasElseStatement) {
        super(ifStatement);
        this.hasElseStatement = hasElseStatement;
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public boolean hasElseStatement() {
        return hasElseStatement;
    }
}
