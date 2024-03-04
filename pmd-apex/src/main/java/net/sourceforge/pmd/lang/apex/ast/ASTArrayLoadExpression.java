/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.ArrayExpression;

public final class ASTArrayLoadExpression extends AbstractApexNode.Single<ArrayExpression> {

    ASTArrayLoadExpression(ArrayExpression arrayLoadExpression) {
        super(arrayLoadExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
