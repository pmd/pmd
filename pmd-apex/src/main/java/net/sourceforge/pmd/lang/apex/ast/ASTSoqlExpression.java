/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.SoqlExpression;

public class ASTSoqlExpression extends AbstractApexNode.Single<SoqlExpression> {

    ASTSoqlExpression(SoqlExpression soqlExpression) {
        super(soqlExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getQuery() {
        return node.getQuery();
    }

    public String getCanonicalQuery() {
        return node.getQuery();
    }
}
