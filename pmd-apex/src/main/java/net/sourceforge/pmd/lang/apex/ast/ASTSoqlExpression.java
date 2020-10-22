/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.SoqlExpression;

public final class ASTSoqlExpression extends AbstractApexNode<SoqlExpression> {

    ASTSoqlExpression(SoqlExpression soqlExpression) {
        super(soqlExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getQuery() {
        return node.getRawQuery();
    }

    public String getCanonicalQuery() {
        return node.getCanonicalQuery();
    }
}
