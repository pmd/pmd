/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.StatementExecuted;

public final class ASTStatementExecuted extends AbstractApexNode<StatementExecuted> {

    ASTStatementExecuted(StatementExecuted node) {
        super(node);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
