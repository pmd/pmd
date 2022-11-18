/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.statement.ExpressionStatement;

public class ASTExpressionStatement extends AbstractApexNode.Single<ExpressionStatement> {

    ASTExpressionStatement(ExpressionStatement expression) {
        super(expression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
