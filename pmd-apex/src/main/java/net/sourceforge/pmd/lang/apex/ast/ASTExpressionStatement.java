/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.ExpressionStatement;

public final class ASTExpressionStatement extends AbstractApexNode<ExpressionStatement> {

    ASTExpressionStatement(ExpressionStatement expressionStatement) {
        super(expressionStatement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    private int beginColumnDiff = -1;

    @Override
    public int getBeginColumn() {
        if (beginColumnDiff > -1) {
            return super.getBeginColumn() - beginColumnDiff;
        }

        if (getNumChildren() > 0 && getChild(0) instanceof ASTMethodCallExpression) {
            ASTMethodCallExpression methodCallExpression = (ASTMethodCallExpression) getChild(0);

            int fullLength = methodCallExpression.getFullMethodName().length();
            int nameLength = methodCallExpression.getMethodName().length();
            if (fullLength > nameLength) {
                beginColumnDiff = fullLength - nameLength;
            } else {
                beginColumnDiff = 0;
            }
        }

        return super.getBeginColumn() - beginColumnDiff;
    }
}
