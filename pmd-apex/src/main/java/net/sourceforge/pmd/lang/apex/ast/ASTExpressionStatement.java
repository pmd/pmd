/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.ExpressionStatement;

public class ASTExpressionStatement extends AbstractApexNode<ExpressionStatement> {

    public ASTExpressionStatement(ExpressionStatement expressionStatement) {
        super(expressionStatement);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
