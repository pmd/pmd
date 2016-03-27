package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.MethodCallExpression;

public class ASTMethodCallExpression extends AbstractApexNode<MethodCallExpression> {

    public ASTMethodCallExpression(MethodCallExpression methodCallExpression) {
        super(methodCallExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}