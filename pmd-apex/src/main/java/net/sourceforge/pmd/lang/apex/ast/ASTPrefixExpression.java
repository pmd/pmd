/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.expression.UnaryExpression;

public class ASTPrefixExpression extends AbstractApexNode.Single<UnaryExpression> {

    @Deprecated
    @InternalApi
    public ASTPrefixExpression(UnaryExpression unaryExpression) {
        super(unaryExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public PrefixOperator getOp() {
        return PrefixOperator.valueOf(node.getOp());
    }
}
