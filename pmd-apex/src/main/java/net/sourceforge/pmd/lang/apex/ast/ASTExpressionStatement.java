/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.Expression;

public class ASTExpressionStatement extends AbstractApexNode.Single<Expression> {

    ASTExpressionStatement(Expression expression) {
        super(expression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
