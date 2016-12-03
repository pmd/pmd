/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.MethodCallExpression;

public class ASTMethodCallExpression extends AbstractApexNode<MethodCallExpression> {

    public ASTMethodCallExpression(MethodCallExpression methodCallExpression) {
        super(methodCallExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getMethodName() {
        return getNode().getMethodName();
    }

    public String getFullMethodName() {
        final String methodName = getMethodName();
        String typeName = "";
        if (!getNode().getReferenceExpression().getJadtIdentifiers().isEmpty()) {
            typeName = getNode().getReferenceExpression().getJadtIdentifiers().get(0).value + ".";
        }
        return typeName + methodName;
    }
}
