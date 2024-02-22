/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.UnaryExpression;

public final class ASTPrefixExpression extends AbstractApexNode.Single<UnaryExpression> {

    ASTPrefixExpression(UnaryExpression unaryExpression) {
        super(unaryExpression);
    }

    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public PrefixOperator getOp() {
        return PrefixOperator.valueOf(node.getOp());
    }
}
