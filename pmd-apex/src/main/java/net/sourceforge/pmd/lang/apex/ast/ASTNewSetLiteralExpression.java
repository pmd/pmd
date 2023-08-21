/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.NewSetLiteralExpression;

public final class ASTNewSetLiteralExpression extends AbstractApexNode<NewSetLiteralExpression> {

    ASTNewSetLiteralExpression(NewSetLiteralExpression newSetLiteralExpression) {
        super(newSetLiteralExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
