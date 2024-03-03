/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.UnaryExpression;

public final class ASTPostfixExpression extends AbstractApexNode.Single<UnaryExpression> {

    ASTPostfixExpression(UnaryExpression unaryExpression) {
        super(unaryExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public PostfixOperator getOp() {
        return PostfixOperator.valueOf(node.getOp());
    }
}
