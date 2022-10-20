/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.CastExpression;

public class ASTCastExpression extends AbstractApexNode.Single<CastExpression> {

    ASTCastExpression(CastExpression castExpression) {
        super(castExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
