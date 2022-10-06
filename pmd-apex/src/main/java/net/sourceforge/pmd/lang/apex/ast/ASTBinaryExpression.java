/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.expression.BinaryExpression;

public class ASTBinaryExpression extends AbstractApexNode.Single<BinaryExpression> {

    @Deprecated
    @InternalApi
    public ASTBinaryExpression(BinaryExpression binaryExpression) {
        super(binaryExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public BinaryOperator getOp() {
        return BinaryOperator.valueOf(node.getOp());
    }
}
