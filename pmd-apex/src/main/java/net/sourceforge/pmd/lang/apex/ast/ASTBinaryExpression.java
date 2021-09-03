/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.data.ast.BinaryOp;
import apex.jorje.semantic.ast.expression.BinaryExpression;

public final class ASTBinaryExpression extends AbstractApexNode<BinaryExpression> {

    ASTBinaryExpression(BinaryExpression binaryExpression) {
        super(binaryExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public BinaryOp getOperator() {
        return node.getOp();
    }
}
