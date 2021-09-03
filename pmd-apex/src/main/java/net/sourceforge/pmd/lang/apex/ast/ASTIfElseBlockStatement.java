/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.IfElseBlockStatement;

public final class ASTIfElseBlockStatement extends AbstractApexNode<IfElseBlockStatement> {

    ASTIfElseBlockStatement(IfElseBlockStatement ifElseBlockStatement) {
        super(ifElseBlockStatement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public boolean hasElseStatement() {
        return node.hasElseStatement();
    }
}
