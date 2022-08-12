/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.expression.BinaryExpression;

public class ASTBooleanExpression extends AbstractApexNode.Single<BinaryExpression> {

    @Deprecated
    @InternalApi
    public ASTBooleanExpression(BinaryExpression booleanExpression) {
        super(booleanExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public BooleanOperator getOp() {
        return BooleanOperator.valueOf(this.node.getOp());
    }
}
