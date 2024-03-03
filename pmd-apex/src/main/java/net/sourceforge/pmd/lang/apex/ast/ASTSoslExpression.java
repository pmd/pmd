/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.SoslExpression;

public final class ASTSoslExpression extends AbstractApexNode.Single<SoslExpression> {

    ASTSoslExpression(SoslExpression soslExpression) {
        super(soslExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
