/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.NewMapInitExpression;

public final class ASTNewMapInitExpression extends AbstractApexNode<NewMapInitExpression> {

    ASTNewMapInitExpression(NewMapInitExpression newMapInitExpression) {
        super(newMapInitExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
