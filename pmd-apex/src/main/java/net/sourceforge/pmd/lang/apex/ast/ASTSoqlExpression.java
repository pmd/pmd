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
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getQuery() {
        return node.getRawQuery();
    }

    public String getCanonicalQuery() {
        return node.getCanonicalQuery();
    }
}
