/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Iterator;

import apex.jorje.data.Identifier;
import apex.jorje.semantic.ast.expression.MethodCallExpression;


public final class ASTMethodCallExpression extends AbstractApexNode<MethodCallExpression> {
    ASTMethodCallExpression(MethodCallExpression methodCallExpression) {
        super(methodCallExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getMethodName() {
        return node.getMethodName();
    }

    public String getFullMethodName() {
        final String methodName = getMethodName();
        StringBuilder typeName = new StringBuilder();
        for (Iterator<Identifier> it = node.getReferenceContext().getNames().iterator(); it.hasNext();) {
            typeName.append(it.next().getValue()).append('.');
        }
        return typeName.toString() + methodName;
    }

    public int getInputParametersSize() {
        return node.getInputParameters().size();
    }
}
