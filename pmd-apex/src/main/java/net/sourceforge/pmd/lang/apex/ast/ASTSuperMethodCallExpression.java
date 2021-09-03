/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.SuperMethodCallExpression;

public final class ASTSuperMethodCallExpression extends AbstractApexNode<SuperMethodCallExpression> {

    ASTSuperMethodCallExpression(SuperMethodCallExpression superMethodCallExpression) {
        super(superMethodCallExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
