/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.BinaryExpression;

public final class ASTInstanceOfExpression extends AbstractApexNode.Single<BinaryExpression> {

    ASTInstanceOfExpression(BinaryExpression binaryExpression) {
        super(binaryExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
